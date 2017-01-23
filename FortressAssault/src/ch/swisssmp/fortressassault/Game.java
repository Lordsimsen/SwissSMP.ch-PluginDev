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
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.ChatColor;

@SuppressWarnings("deprecation")
public class Game implements Listener{
	protected final Random random = new Random();
	
	private static final HashMap<UUID, Integer> players = new HashMap<UUID, Integer>();
	protected static final HashMap<UUID, FortressTeam> teamMap = new HashMap<UUID, FortressTeam>();
	protected final Scoreboard scoreboard;
	protected final Objective objective;
	
	private GameState gameState = GameState.PREGAME;

	private BukkitTask endBuildPhaseWarningTask = null;
	private BukkitTask endBuildPhaseTask = null;
	private long endBuildPhaseTaskStartTime = 0;
	
	public Game(){
		if(getInstance()!=null){
			deleteInstance();
		}
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		this.objective = scoreboard.registerNewObjective("dummy", "Punkte");
		this.objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
        createInstance();
        for(FortressTeam team : FortressTeam.teams.values()){
        	team.reset();
        	team.registerTeam(this);
        }
	}
	@EventHandler(ignoreCancelled=true)
	private void onItemDrop(PlayerDropItemEvent event){
		if(event.getPlayer().getGameMode()==GameMode.CREATIVE) return;
		if(event.getItemDrop().getItemStack().getType()==Main.crystalMaterial) event.setCancelled(true);
	}
	@EventHandler(ignoreCancelled=true)
	private void onRegionEnter(RegionEnterEvent event){
		ProtectedRegion region = event.getRegion();
		String regionName = region.getId();
		Player player = event.getPlayer();
		if(player.getGameMode()==GameMode.CREATIVE) return;
		if(regionName.contains("team")&&regionName.contains("_")){
			//first leave the old team if there was any
			if(this.gameState==GameState.PREGAME){
				FortressTeam oldTeam = teamMap.get(player.getUniqueId());
				if(oldTeam!=null)oldTeam.leave(player.getUniqueId());
			}
			
			String team_id = regionName.split("_")[1];
			//special section for spectators
			if(team_id.equals("spectate")){
				player.setFallDistance(0);
				player.teleport(getPoint(getInstance(), "spectate"));
				return;
			}
			//do nothing if the player just wants to leave the team
			else if(team_id.equals("neutral")){
				
				player.teleport(getPoint(getLobby(), "lobby"));
				return;
			}
			else if(team_id.equals("info")){
				sendGameState(player);
				for(FortressTeam team : FortressTeam.teams.values()){
					player.sendMessage("- "+team.color+team.name+ChatColor.RESET+": "+team.player_uuids.size()+" Mitspieler");
				}
				return;
			}
			//join the new team
			else if(this.gameState==GameState.PREGAME || this.gameState==GameState.FINISHED){
				FortressTeam fortressTeam = FortressTeam.get(Integer.parseInt(team_id));
				if(fortressTeam==null) fortressTeam = new FortressTeam(Main.getYamlResponse("fortress_assault/team.php", new String[]{
						"team="+team_id
				}).getConfigurationSection(team_id));
				fortressTeam.registerTeam(this);
				fortressTeam.join(player);
				return;
			}
		}
		else if(regionName.contains("class")&&regionName.contains("_")){
			int class_id = Integer.valueOf(regionName.split("_")[1]);
			PlayerClass playerClass = PlayerClass.get(class_id);
			if(playerClass==null) {
				players.remove(player.getUniqueId());
			}
			else {
				players.put(player.getUniqueId(), playerClass.class_id);
				Main.sendActionBar(player, "Du bist nun "+ChatColor.AQUA+playerClass.name+ChatColor.RESET+"!");
			}
			PlayerClass.setItems(player, playerClass, gameState);
		}
	}
	@EventHandler
	private void onPlayerInteractBlock(PlayerInteractEvent event){
		if(event.getAction()!=Action.LEFT_CLICK_BLOCK && event.getAction()!=Action.RIGHT_CLICK_BLOCK){
			return;
		}
		Block block = event.getClickedBlock();
		switch(this.gameState){
		case BUILD:
		{
			if(event.getHand()!=EquipmentSlot.HAND){
				return;
			}
			FortressTeam team = FortressTeam.get(block);
			if(block.getType()==Main.crystalMaterial){
				if(team.leader.equals(event.getPlayer().getUniqueId())){
					if(event.getAction()==Action.LEFT_CLICK_BLOCK){
						block.setType(Material.AIR);
						block.getWorld().playEffect(block.getLocation(), Effect.TILE_BREAK, Main.crystalMaterial.getId());
						event.getPlayer().getInventory().addItem(new ItemStack(Main.crystalMaterial, 1));
						team.setReady(false);
						team.crystal = null;
					}
					else if(event.getAction()==Action.RIGHT_CLICK_BLOCK){
						team.toggleReady();
					}
				}
				else{
					if(event.getAction()==Action.LEFT_CLICK_BLOCK){
						Main.sendActionBar(event.getPlayer(), ChatColor.RED+"Nur der Team-Leader kann den Kristall verschieben!");
					}
					else if(event.getAction()==Action.RIGHT_CLICK_BLOCK){
						Main.sendActionBar(event.getPlayer(), ChatColor.RED+"Nur der Team-Leader kann die Bauphase beenden.");
					}
				}
				event.setCancelled(true);
			}
			else if(event.getAction()==Action.LEFT_CLICK_BLOCK){
				if(block.getType()!=Material.SMOOTH_BRICK){
					//Main.sendActionBar(event.getPlayer(), ChatColor.RED+"Du kannst nur Steinziegel abbauen.");
					return;
				}
				else{
					block.breakNaturally();
					event.setCancelled(true);
				}
			}
			break;
		}
		case FIGHT:
		{
			FortressTeam owningTeam = FortressTeam.get(block);
			if(owningTeam!=null){
				if(owningTeam.player_uuids.contains(event.getPlayer().getUniqueId())){
					//defuse
					owningTeam.setFused(false, event.getPlayer());
				}
				else{
					//fuse
					owningTeam.setFused(true, event.getPlayer());
				}
			}
			else{
				if(block.getType()==Material.DOUBLE_PLANT)
					event.setCancelled(false);
				else if(block.getType()==Material.LONG_GRASS){
					event.setCancelled(false);
				}
			}
			break;
		}
		default:
			return;
		}
	}
	@EventHandler(ignoreCancelled=true)
	private void onBlockPlace(BlockPlaceEvent event){
		Block block = event.getBlock();
		if(block.getType()!=Main.crystalMaterial) return;
		event.setCancelled(true);
		Player player = event.getPlayer();
		FortressTeam fortressTeam = teamMap.get(player.getUniqueId());
		if(fortressTeam!=null){
			if(fortressTeam.leader.equals(player.getUniqueId())){
				WorldGuardPlugin worldGuard = Main.worldGuardPlugin;
				RegionManager regionManager = worldGuard.getRegionManager(getInstance());
				ProtectedRegion region = regionManager.getRegion("base_"+fortressTeam.team_id);
				if(region.contains(block.getX(), block.getY(), block.getZ())){
					fortressTeam.crystal = block;
					event.setCancelled(false);
				}
				else{
					Main.sendActionBar(player, ChatColor.RED+"Platziere den Kristall in deiner Basis.");
				}
			}
		}
	}
	@EventHandler(ignoreCancelled=true)
	private void onPlayerDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		FortressTeam team = teamMap.get(player.getUniqueId());
		if(this.gameState==GameState.BUILD && team!=null){
			Vector vector = team.getSpawn();
			player.setBedSpawnLocation(new Location(getInstance(), vector.getX(), vector.getY(), vector.getZ()), true);
			return;
		}
		if(this.gameState!=GameState.FIGHT){
			event.getEntity().setBedSpawnLocation(getPoint(getLobby(), "lobby"), true);
			return;
		}
		EntityType killerType = player.getLastDamageCause().getEntityType();
		if(killerType == EntityType.PLAYER){
			Player killer = event.getEntity().getKiller();
			this.addScore(killer, Main.config.getInt("scores.player_kill"), player.getName()+" getötet");
		}
		event.setKeepInventory(true);
		
		if(team==null){
			player.setBedSpawnLocation(getPoint(getLobby(), "lobby"), true);
		}
		else{
			Vector vector = team.getSpawn();
			player.setBedSpawnLocation(new Location(getInstance(), vector.getX(), vector.getY(), vector.getZ()), true);
			player.setGameMode(GameMode.SPECTATOR);
			Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable(){
				public void run(){
					player.setGameMode(GameMode.SURVIVAL);
				}
			}, Main.config.getInt("death_timeout")*20L);
		}
	}
	@EventHandler(ignoreCancelled=true)
	private void onBlockBreak(BlockBreakEvent event){
		if(event.getPlayer().getGameMode()==GameMode.CREATIVE){
			return;
		}
		if(event.getBlock().getType()!=Material.SMOOTH_BRICK) event.setCancelled(true);
	}
	@EventHandler(ignoreCancelled=true)
	private void onPlayerLogin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		FortressTeam team = teamMap.get(player.getUniqueId());
		ChatColor playerNameColor;
		if(team==null){
			playerNameColor = ChatColor.WHITE;
			player.sendMessage(ChatColor.DARK_AQUA+"Willkommen in Fortress Assault!");
		}
		else{
			playerNameColor = team.color;
		}
		event.setJoinMessage(ChatColor.GREEN+"+ "+ChatColor.RESET+"["+playerNameColor+player.getDisplayName()+ChatColor.RESET+"]");
		updatePlayer(player);
	}
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		FortressTeam team = teamMap.get(player.getUniqueId());
		ChatColor playerNameColor;
		if(team==null){
			playerNameColor = ChatColor.WHITE;
			player.sendMessage(ChatColor.DARK_AQUA+"Willkommen in Fortress Assault!");
		}
		else{
			playerNameColor = team.color;
		}
		event.setQuitMessage(ChatColor.RED+"- "+ChatColor.RESET+"["+playerNameColor+player.getDisplayName()+ChatColor.RESET+"]");
		if(team==null) return;
		if(this.gameState==GameState.BUILD||this.gameState==GameState.FIGHT){
			return;
		}
		else{
			team.leave(player.getUniqueId());
		}
		
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
				Main.sendTitle(player, "SIEG!", this.getMvpInfo(), 1, 5, 1);
			}
		}
		Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable(){
			public void run(){
				advance(GameState.FINISHED);
			}
		}, 60L);
	}
	private void advance(GameState newState){
		if(this.gameState==newState) return;
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
					if(!players.containsKey(player_uuid)){
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

		for(UUID player_uuid : players.keySet()){
			updatePlayer(Bukkit.getPlayer(player_uuid));
		}
		if(this.gameState==GameState.BUILD){
			for(FortressTeam team : FortressTeam.teams.values()){
				team.leader = team.player_uuids.get(0);
				Player player = Bukkit.getPlayer(team.leader);
				if(player==null) continue;
				player.getInventory().addItem(new ItemStack(Main.crystalMaterial, 1));
				player.sendMessage(ChatColor.GREEN+"Du bist Team-Leader!");
				player.sendMessage(ChatColor.GOLD+"Platziere den Kristall an einem geschützten Ort.");
				player.sendMessage(ChatColor.YELLOW+"Rechtsklicke auf den Kristall, sobald dein Team fertig gebaut hat, um die Bauphase zu beenden.");
				for(UUID player_uuid : team.player_uuids){
					Player teamMember = Bukkit.getPlayer(player_uuid);
					if(teamMember==null) continue;
					teamMember.setFallDistance(0);
					teamMember.setBedSpawnLocation(getPoint(getInstance(), "spectate"), true);
					teamMember.teleport(new Location(getInstance(), team.spawn.getX(), team.spawn.getY(), team.spawn.getZ()));
				}
			}

			this.endBuildPhaseWarningTask = Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable(){
				public void run(){
					for(UUID player_uuid : players.keySet()){
						Player player = Bukkit.getPlayer(player_uuid);
						if(player!=null) Main.sendTitle(player, "", ChatColor.DARK_PURPLE+"Noch 60 Sekunden!", 1, 3, 1);
					}
					endBuildPhaseWarningTask = null;
				}
			}, (Math.max((Main.config.getInt("buildphase")-1)*60*20, 1)));
			this.endBuildPhaseTask = Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable(){
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
			}, (Math.max((Main.config.getInt("buildphase"))*60*20, 1)));
			endBuildPhaseTaskStartTime = System.currentTimeMillis();
		}
		else if(this.gameState==GameState.FIGHT){
			World world = getInstance();
				for(Entity entity : world.getEntities()){
					if(entity instanceof Item){
						entity.remove();
					}
				}
		}
		else if(this.gameState==GameState.FINISHED){
			deleteInstance();
			Game game = this;
			Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable(){
				public void run(){
					HandlerList.unregisterAll(game);
					Main.game = new Game();
				}
			}, 40);
		}
	}
	private void updatePlayer(Player player){
		if(player==null) return;
		FortressTeam fortressTeam = teamMap.get(player.getUniqueId());
		switch(this.gameState){
		case PREGAME:
			player.teleport(getPoint(getLobby(), "lobby"));
			player.setGameMode(GameMode.ADVENTURE);
			break;
		case BUILD:
			if(fortressTeam==null){
				player.teleport(getPoint(getInstance(), "spectate"));
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
				player.teleport(getPoint(getInstance(), "spectate"));
				break;
			}
			player.setGameMode(GameMode.SURVIVAL);
			break;
		case FINISHED:
			player.teleport(getPoint(getLobby(), "lobby"));
			player.setGameMode(GameMode.ADVENTURE);
			break;
		}
		PlayerClass playerClass;
		if(players.containsKey(player.getUniqueId())){
			playerClass = PlayerClass.get(players.get(player.getUniqueId()));
		}
		else{
			playerClass = null;
		}
		PlayerClass.setItems(player, playerClass, this.gameState);
		sendGameState(player);
	}
	protected boolean isFinished(){
		return this.gameState==GameState.FINISHED;
	}
	protected void sendGameState(Player player){
		FortressTeam team = teamMap.get(player.getUniqueId());
		switch(this.gameState){
		case PREGAME:
			player.sendMessage(ChatColor.DARK_AQUA+"Momentan läuft keine Partie.");
			if(team==null) player.sendMessage(ChatColor.GOLD+"Du kannst einem Team beitreten.");
			break;
		case BUILD:
			if(team!=null){
				if(!team.isReady()){
					Main.sendTitle(player, "Aufbau", ChatColor.GOLD+"Baue eine Burg", 1, 3, 1);
					int remaining = Main.config.getInt("buildphase");
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
				Main.sendTitle(player, "Belagerung", ChatColor.GOLD+"Zerstöre den feindlichen Kristall!", 1, 3, 1);
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
	private Location getPoint(World world, String name){
		ConfigurationSection dataSection = Main.config.getConfigurationSection(name);
		Vector vector = getVector(dataSection);
		return new Location(world, vector.getX(), vector.getY(), vector.getZ());
	}
	private Vector getVector(ConfigurationSection dataSection){
		double x = dataSection.getDouble("x");
		double y = dataSection.getDouble("y");
		double z = dataSection.getDouble("z");
		double random = dataSection.getDouble("random");
		return new Vector(getRandomValue(x, random), y+0.5, getRandomValue(z, random));
	}
	private double getRandomValue(double base, double random){
		return base-random+2*this.random.nextDouble()*random*2;
	}
	protected GameState getGameState(){
		return this.gameState;
	}
	protected String getMvpInfo(){
		String mvp = bestPlayer();
		Integer score = objective.getScore(mvp).getScore();
		return ChatColor.YELLOW+"MVP: "+mvp+" ("+score+" Punkte)";
	}
	protected void addScore(Player player, int score, String reason){
		if(player==null) return;
		Score s = objective.getScore(player);
		s.setScore(s.getScore()+score);
		Main.sendActionBar(player, ChatColor.GREEN+"+"+score+ChatColor.RESET+" "+reason);
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
		Location leavePoint = getLobby().getSpawnLocation();
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
			Bukkit.getScheduler().runTaskLater(Main.plugin, task, 20L);
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
		Bukkit.getScheduler().runTaskLater(Main.plugin, runnable, delay);
	}
	private void deleteInstance(){
		delete(Bukkit.getWorld("FortressAssaultArena"), getLobby().getSpawnLocation(), true);
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
			WorldGuardPlugin worldGuard = Main.worldGuardPlugin;
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
	}
	private File getTemplateDirectory(){
		return new File(Main.dataFolder, "template/FortressAssaultTemplate");
	}
	private File getWorldDirectory(){
		return new File(Bukkit.getWorldContainer(), "FortressAssaultArena");
	}
	public File getWorldguardDirectory(String name){
		WorldGuardPlugin worldGuard = Main.worldGuardPlugin;
		return new File(worldGuard.getDataFolder(), "worlds/FortressAssault"+name);
	}
	protected static World getLobby(){
		return Bukkit.getWorld("FortressAssault");
	}
	private World getInstance(){
		return Bukkit.getWorld("FortressAssaultArena");
	}
	private static void copyDirectory(File source, File target){
        ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
		FileUtil.copyDirectory(source, target, ignore);
	}
	//static stuff
	private static void deleteFiles(File path){
		Runnable task = new Runnable(){
			public void run(){
				FileUtil.deleteRecursive(path);
			}
		};
		Bukkit.getScheduler().runTaskLater(Main.plugin, task, 20);
	}
}
