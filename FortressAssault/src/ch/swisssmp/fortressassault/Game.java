package ch.swisssmp.fortressassault;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.swisssmp.utils.SwissSMPler;

import org.bukkit.ChatColor;

@SuppressWarnings("deprecation")
public class Game implements Listener{
	protected final Random random = new Random();
	
	protected final Scoreboard scoreboard;
	protected final Objective objective;
	
	private GameState gameState = GameState.PREGAME;

	private BukkitTask endBuildPhaseWarningTask = null;
	private BukkitTask endBuildPhaseTask = null;
	private long endBuildPhaseTaskStartTime = 0;
	
	private EventListener eventListener;
	
	public Game(){
		if(getInstance()!=null){
			deleteInstance();
		}
		this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		for(Objective objective : scoreboard.getObjectives()) objective.unregister();
		this.objective = scoreboard.registerNewObjective(FortressAssault.scoreSymbol, "dummy");
		this.objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		Bukkit.getPluginManager().registerEvents(this, FortressAssault.plugin);
        createInstance();
        for(FortressTeam team : FortressTeam.teams.values()){
        	team.reset();
        	team.registerTeam(this);
        }
        this.eventListener = new EventListener(this);
        Bukkit.getPluginManager().registerEvents(this.eventListener, FortressAssault.plugin);
	}
	
	protected boolean isBlockInteractionAllowed(UUID player_uuid, Block block){
		RegionManager regionManager = FortressAssault.worldGuardPlugin.getRegionManager(block.getWorld());
		int team_id;
		
		for(ProtectedRegion protectedRegion : regionManager.getApplicableRegions(block.getLocation())){
			if(protectedRegion.getId().contains("base_")){
				team_id = Integer.parseInt(protectedRegion.getId().split("_")[1]);
				if(FortressTeam.get(team_id).player_uuids.contains(player_uuid)){
					return true;
				}
			}
		}
		return false;
	}
	protected void setBuildphase(){
		this.advance(GameState.BUILD);
	}
	protected void setFightphase(){
		this.advance(GameState.FIGHT);
	}
	protected void setFinished(FortressTeam winner){
		if(winner!=null){
			winner.setFused(false, null);
			for(UUID player_uuid : winner.player_uuids){
				Player player = Bukkit.getPlayer(player_uuid);
				if(player==null) continue;
				SwissSMPler.get(player).sendTitle("SIEG!", this.getMvpInfo());
			}
		}
		for(FortressTeam team : FortressTeam.teams.values()){
			team.purgeDisconnected();
		}
		Bukkit.getScheduler().runTaskLater(FortressAssault.plugin, new Runnable(){
			public void run(){
				advance(GameState.FINISHED);
			}
		}, 60L);
	}
	private void advance(GameState newState){
		if(this.gameState==newState) return;
		for(Player player : Bukkit.getOnlinePlayers()){
			for (PotionEffect effect : player.getActivePotionEffects())
		        player.removePotionEffect(effect.getType());
		}
		if(this.endBuildPhaseWarningTask!=null)
			this.endBuildPhaseWarningTask.cancel();
		if(this.endBuildPhaseTask!=null)
			this.endBuildPhaseTask.cancel();
		if(this.gameState==GameState.PREGAME && (newState==GameState.BUILD || newState==GameState.FIGHT)){
			if(FortressTeam.teams.size()<2){
				Bukkit.broadcastMessage(ChatColor.DARK_AQUA+"Das Spiel kann nicht fortschreiten, da zu wenig Teams vorhanden sind.");
				return;
			}
			ArrayList<String> guiltyPlayers = new ArrayList<String>();
			HashMap<FortressTeam, UUID> kickPlayers = new HashMap<FortressTeam, UUID>();
			for(FortressTeam team : FortressTeam.teams.values()){
				for(UUID player_uuid : team.player_uuids){
					if(!FortressAssault.players.containsKey(player_uuid)){
						Player player = Bukkit.getPlayer(player_uuid);
						if(player==null) kickPlayers.put(team, player_uuid);
						else guiltyPlayers.add(player.getDisplayName());
					}
				}
			}
			for(Entry<FortressTeam,UUID> entry : kickPlayers.entrySet()){
				entry.getKey().leave(entry.getValue());
			}
			if(!guiltyPlayers.isEmpty()){
				Bukkit.broadcastMessage(ChatColor.DARK_AQUA+"Folgende Spieler müssen noch eine Klasse aussuchen:");
				for(String playerName : guiltyPlayers){
					Bukkit.broadcastMessage("- "+playerName);
				}
				return;
			}
		}
		this.gameState = newState;

		Player player;
		for(UUID player_uuid : FortressAssault.players.keySet()){
			player = Bukkit.getPlayer(player_uuid);
			if(player==null) continue;
			player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			updatePlayer(player);
			updateInventory(player);
		}
		if(this.gameState==GameState.BUILD){
			for(FortressTeam team : FortressTeam.teams.values()){
				team.purgeDisconnected();
				team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
				if(team.player_uuids.size()>0){
					team.leader = team.player_uuids.get(0);
				}
				else{
					this.setFinished(null);
					return;
				}
				player = Bukkit.getPlayer(team.leader);
				if(player==null) continue;
				player.getInventory().addItem(new ItemStack(FortressAssault.crystalMaterial, 1));
				player.sendMessage(ChatColor.GREEN+"Du bist Team-Leader!");
				player.sendMessage(ChatColor.GOLD+"Platziere den Kristall an einem geschützten Ort.");
				player.sendMessage(ChatColor.YELLOW+"Rechtsklicke auf den Kristall, sobald dein Team fertig gebaut hat, um die Bauphase zu beenden.");
				for(UUID player_uuid : team.player_uuids){
					Player teamMember = Bukkit.getPlayer(player_uuid);
					if(teamMember==null) continue;
					teamMember.setFallDistance(0);
					teamMember.setBedSpawnLocation(FortressAssault.getPoint(getInstance(), "spectate"), true);
					teamMember.teleport(new Location(getInstance(), team.spawn.getX(), team.spawn.getY(), team.spawn.getZ()));
				}
			}

			this.endBuildPhaseWarningTask = Bukkit.getScheduler().runTaskLater(FortressAssault.plugin, new Runnable(){
				public void run(){
					for(UUID player_uuid : FortressAssault.players.keySet()){
						Player player = Bukkit.getPlayer(player_uuid);
						if(player!=null) SwissSMPler.get(player).sendTitle("", ChatColor.DARK_PURPLE+"Noch 60 Sekunden!");
					}
					endBuildPhaseWarningTask = null;
				}
			}, (Math.max((FortressAssault.config.getInt("buildphase")-1)*60*20, 1)));
			this.endBuildPhaseTask = Bukkit.getScheduler().runTaskLater(FortressAssault.plugin, new Runnable(){
				public void run(){
					ArrayList<FortressTeam> readyTeams = new ArrayList<FortressTeam>();
					for(FortressTeam team : FortressTeam.teams.values()){
						if(team.isReady()) readyTeams.add(team);
						else{
							team.setLost(null);
						}
					}
					if(readyTeams.size()>1){
						setFightphase();
					}
					else if(readyTeams.size()==1 && gameState!=GameState.FINISHED){
						setFinished(readyTeams.get(0));
					}
					else if(gameState!=GameState.FINISHED){
						setFinished(null);
					}
					endBuildPhaseTask = null;
				}
			}, (Math.max((FortressAssault.config.getInt("buildphase"))*60*20, 1)));
			endBuildPhaseTaskStartTime = System.currentTimeMillis();
		}
		else if(this.gameState==GameState.FIGHT){
			for(FortressTeam team : FortressTeam.teams.values()){
				team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
			}
			World world = getInstance();
			for(Entity entity : world.getEntities()){
				if(entity instanceof Item){
					entity.remove();
				}
			}
			RegionManager regionManager = FortressAssault.worldGuardPlugin.getRegionManager(this.getInstance());
			regionManager.removeRegion("blockade");
		}
		else if(this.gameState==GameState.FINISHED){
			for(FortressTeam team : FortressTeam.teams.values()){
				team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
			}
			deleteInstance();
			Bukkit.getScheduler().runTaskLater(FortressAssault.plugin, new Runnable(){
				public void run(){
					HandlerList.unregisterAll(eventListener);
					FortressAssault.game = new Game();
				}
			}, 40);
		}
	}
	protected void updatePlayer(Player player){
		if(player==null) return;
		FortressTeam fortressTeam = FortressAssault.teamMap.get(player.getUniqueId());
		FortressAssault.updateTabList(player, fortressTeam);
		switch(this.gameState){
		case PREGAME:
			player.teleport(FortressAssault.getPoint(FortressAssault.getLobby(), "lobby"));
			player.setGameMode(GameMode.ADVENTURE);
			this.scoreboard.resetScores(player.getName());
			break;
		case BUILD:
			if(fortressTeam==null){
				player.teleport(FortressAssault.getPoint(getInstance(), "spectate"));
				this.scoreboard.resetScores(player.getName());
				break;
			}
			else{
				if(fortressTeam.isReady()){
					player.setGameMode(GameMode.ADVENTURE);
				}
				else{
					player.setGameMode(GameMode.SURVIVAL);
				}
			}
			break;
		case FIGHT:
			if(fortressTeam==null){
				player.teleport(FortressAssault.getPoint(getInstance(), "spectate"));
				this.scoreboard.resetScores(player.getName());
				break;
			}
			player.setGameMode(GameMode.ADVENTURE);
			break;
		case FINISHED:
			player.teleport(FortressAssault.getPoint(FortressAssault.getLobby(), "lobby"));
			player.setGameMode(GameMode.ADVENTURE);
			this.scoreboard.resetScores(player.getName());
			break;
		}
		sendGameState(player);
	}
	protected void updateInventory(Player player){
		PlayerClass playerClass;
		if(FortressAssault.players.containsKey(player.getUniqueId())){
			playerClass = PlayerClass.get(FortressAssault.players.get(player.getUniqueId()));
		}
		else{
			playerClass = null;
		}
		PlayerClass.setItems(player, playerClass, this.gameState);
	}
	protected boolean isFinished(){
		return this.gameState==GameState.FINISHED;
	}
	protected void sendGameState(Player player){
		FortressTeam team = FortressAssault.teamMap.get(player.getUniqueId());
		switch(this.gameState){
		case PREGAME:
			player.sendMessage(ChatColor.DARK_AQUA+"Momentan läuft keine Partie.");
			if(team==null) player.sendMessage(ChatColor.GOLD+"Du kannst einem Team beitreten.");
			break;
		case BUILD:
			if(team!=null){
				if(!team.isReady()){
					SwissSMPler.get(player).sendTitle("Aufbau", ChatColor.GOLD+"Baue eine Burg");
					int remaining = FortressAssault.config.getInt("buildphase");
					if(this.endBuildPhaseTask!=null){
						long remainingMillis = System.currentTimeMillis()-endBuildPhaseTaskStartTime;
						remaining = remaining-Math.round(remainingMillis/1000/60);
					}
					player.sendMessage(ChatColor.DARK_PURPLE+String.valueOf(remaining)+" Minuten verbleiben für den Bau einer Burg.");
				}
				else{
					player.sendMessage(ChatColor.GREEN+"Dein Team ist bereit. Bitte warten...");
				}
			}
			else{
				player.sendMessage(ChatColor.DARK_AQUA+"Das Spiel befindet sich in der Bauphase.");
				player.sendMessage(ChatColor.GRAY+"Bitte warte, bis die Partie vorbei ist.");
			}
			break;
		case FIGHT:
			player.sendMessage(ChatColor.DARK_AQUA+"Das Spiel befindet sich in der Kampfphase.");
			if(team!=null){
				SwissSMPler.get(player).sendTitle("Belagerung", ChatColor.GOLD+"Zerstöre den feindlichen Kristall!");
				player.sendMessage(ChatColor.GOLD+"Zerstöre den feindlichen Kristall, bevor sie euren vernichten!");
			}
			else{
				player.sendMessage(ChatColor.GRAY+"Bitte warte, bis die Partie vorbei ist.");
			}
			break;
		case FINISHED:
			player.sendMessage(ChatColor.DARK_AQUA+"Das Spiel ist vorbei. Bitte warte, bis ein Spielleiter eine neue Partie startet.");
			break;
		}
	}
	protected GameState getGameState(){
		return this.gameState;
	}
	protected String getMvpInfo(){
		String mvp = bestPlayer();
		Integer score = objective.getScore(mvp).getScore();
		return ChatColor.YELLOW+"MVP: "+mvp+ChatColor.RESET+ChatColor.YELLOW+" ("+score+FortressAssault.scoreSymbol+ChatColor.YELLOW+")";
	}
	protected void addScore(Player player, int score, String reason){
		if(player==null) return;
		Score s = objective.getScore(player);
		s.setScore(s.getScore()+score);
		SwissSMPler.get(player).sendActionBar(ChatColor.GREEN+"+"+score+FortressAssault.scoreSymbol+" "+reason);
		player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 50, random.nextFloat()/0.2f+0.5f+score/1000f);
	}
	protected String bestPlayer() {
		HashMap<String, Integer> scores = new HashMap<String, Integer>();
	    for(Player player : Bukkit.getOnlinePlayers()) {
	        scores.put(player.getName(), objective.getScore(player).getScore());
	    }
	    ArrayList<Integer> values = new ArrayList<Integer>();
	    for(Integer all : scores.values()) {
	        values.add(all);
	    }
	    Collections.sort(values);
	    Collections.reverse(values);
	    return getKey(values.get(0), scores);
	}
	 
	protected String getKey(Integer value, HashMap<String, Integer> hs) {
	    String key = "";
	    for(String keys : hs.keySet()) {
	        if(hs.get(keys) == value) {
	            key = keys;
	        }
	    }
	    return key;
	}
	public World createTemplate(){
		String template_name = "FortressAssaultTemplate";
		if(Bukkit.getWorld(template_name)!=null){
			return Bukkit.getWorld(template_name);
		}
		WorldCreator templateCreator = new WorldCreator(template_name);
		templateCreator.generateStructures(false);
		templateCreator.type(WorldType.FLAT);
		World world = Bukkit.getServer().createWorld(templateCreator);
		applyGamerules(world);
		return world;
	}
	public World editTemplate(){
		String template_name = "FortressAssaultTemplate";
		if(Bukkit.getWorld(template_name)!=null){
			return Bukkit.getWorld(template_name);
		}
		File source = this.getTemplateDirectory();
		if(source.exists() && source.isDirectory()){
			File target = new File(Bukkit.getWorldContainer(), template_name);
			if(!target.exists()){
				copyDirectory(source, target);
			}
			World world = Bukkit.getServer().createWorld(new WorldCreator(template_name));
			applyGamerules(world);
			return world;
		}
		else return createTemplate();
	}
	public boolean saveTemplate(){
		String template_name = "FortressAssaultTemplate";
		World world = Bukkit.getWorld(template_name);
		if(world==null){
			return false;
		}
		world.save();
		Location leavePoint = FortressAssault.getLobby().getSpawnLocation();
		File source = new File(Bukkit.getWorldContainer(), template_name);
		if(source.exists() && source.isDirectory()){
			File target = this.getTemplateDirectory();
			Runnable task = new Runnable(){
				@Override
				public void run(){
					if(target.exists()){
						FileUtil.deleteRecursive(target);
					}
					copyDirectory(source, target);
					delete(Bukkit.getWorld(template_name), leavePoint, false);
				}};
			Bukkit.getScheduler().runTaskLater(FortressAssault.plugin, task, 20L);
			return true;
		}
		else return false;
	}
	private void createInstance(){
		long delay = 1;
		if(getInstance()!=null){
			delay = 40;
			deleteInstance();
		}
		Runnable runnable = new Runnable(){
			public void run(){
				File source = getTemplateDirectory();
				if(source.exists() && source.isDirectory()){
					//copy world data
					File target = getWorldDirectory();
					if(target.exists()){
						FileUtil.deleteRecursive(target);
					}
					copyDirectory(getTemplateDirectory(), getWorldDirectory());
					copyDirectory(getWorldguardDirectory("Template"), getWorldguardDirectory("Arena"));
					World world = Bukkit.createWorld(new WorldCreator("FortressAssaultArena"));
					applyGamerules(world);
					//from this point on everything is going fine
				}
			}
		};
		Bukkit.getScheduler().runTaskLater(FortressAssault.plugin, runnable, delay);
	}
	private void deleteInstance(){
		delete(Bukkit.getWorld("FortressAssaultArena"), FortressAssault.getLobby().getSpawnLocation(), true);
	}
	private boolean delete(World world, Location leavePoint, boolean deleteConfiguration){
		if(world==null) return true;
    	List<Player> players = world.getPlayers();
    	for(Player player : players){
    		player.teleport(leavePoint);
    	}
    	players.clear();
    	String worldName = world.getName();
        if(Bukkit.getServer().unloadWorld(world, !deleteConfiguration)){
			File path = new File(Bukkit.getWorldContainer(), worldName);
	    	deleteFiles(path);
			WorldGuardPlugin worldGuard = FortressAssault.worldGuardPlugin;
	    	if(deleteConfiguration){
				File regionConfiguration = new File(worldGuard.getDataFolder(), "worlds/"+worldName);
				FileUtil.deleteRecursive(regionConfiguration);
	    	}
			worldGuard.reloadConfig();
	    	return true;
        }
        else{
        	return false;
        }
	}
	protected static void applyGamerules(World world){
		world.setGameRuleValue("doMobSpawning", "false");
		world.setGameRuleValue("doDaylightCycle", "false");
		world.setGameRuleValue("doWeatherCycle", "false");
		world.setGameRuleValue("doFireTick", "true");
		world.setGameRuleValue("keepInventory", "true");
		world.setGameRuleValue("showDeathMessages", "false");
		world.setGameRuleValue("announceAdvancements", "false");
	}
	private File getTemplateDirectory(){
		return new File(FortressAssault.dataFolder, "template/FortressAssaultTemplate");
	}
	private File getWorldDirectory(){
		return new File(Bukkit.getWorldContainer(), "FortressAssaultArena");
	}
	public File getWorldguardDirectory(String name){
		WorldGuardPlugin worldGuard = FortressAssault.worldGuardPlugin;
		return new File(worldGuard.getDataFolder(), "worlds/FortressAssault"+name);
	}
	protected World getInstance(){
		return Bukkit.getWorld("FortressAssaultArena");
	}
	private static void copyDirectory(File source, File target){
        ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.lock"));
		FileUtil.copyDirectory(source, target, ignore);
	}
	//static stuff
	private static void deleteFiles(File path){
		Runnable task = new Runnable(){
			public void run(){
				FileUtil.deleteRecursive(path);
			}
		};
		Bukkit.getScheduler().runTaskLater(FortressAssault.plugin, task, 20);
	}
}
