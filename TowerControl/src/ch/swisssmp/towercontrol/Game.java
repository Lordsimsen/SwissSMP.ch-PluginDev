package ch.swisssmp.towercontrol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import ch.swisssmp.utils.SwissSMPler;

import org.bukkit.ChatColor;

public class Game{
	protected final Random random = new Random();
	
	private GameState gameState = GameState.PREGAME;

	private BukkitTask endBuildPhaseWarningTask = null;
	private BukkitTask endBuildPhaseTask = null;
	
	private GameEventListener eventListener;
	
	private Arena arena;
	private final TowerControlTeam teamRed;
	private final TowerControlTeam teamBlue;
	
	public Game(Arena arena, TowerControlTeam teamRed, TowerControlTeam teamBlue){
		this.arena = arena;
        this.teamRed = teamRed;
        this.teamBlue = teamBlue;
        this.eventListener = new GameEventListener(this);
        Bukkit.getPluginManager().registerEvents(this.eventListener, TowerControl.plugin);
		this.arena.reset();
	}
	
	public void setArena(Arena arena){
		if(this.gameState!=GameState.PREGAME) return;
		this.arena = arena;
	}
	
	protected void setFightphase(){
		this.advance(GameState.FIGHT);
	}
	protected void setFinished(TowerControlTeam winner, TowerControlTeam loser){
		if(winner!=null){
			winner.setWon();
		}
		if(loser!=null){
			loser.setLost();
		}
		teamRed.purgeDisconnected();
		teamBlue.purgeDisconnected();
		Bukkit.getScheduler().runTaskLater(TowerControl.plugin, new Runnable(){
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
			player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			player.setFoodLevel(20);
		}
		if(this.endBuildPhaseWarningTask!=null)
			this.endBuildPhaseWarningTask.cancel();
		if(this.endBuildPhaseTask!=null)
			this.endBuildPhaseTask.cancel();
		if(this.gameState==GameState.PREGAME && newState==GameState.FIGHT){
			TowerControl.balanceTeams();
		}
		this.gameState = newState;

		{
			Player player;
			for(UUID player_uuid : TowerControl.getPlayers()){
				player = Bukkit.getPlayer(player_uuid);
				if(player==null) continue;
				player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
				updatePlayer(player);
				updateInventory(player);
			}
		}
		if(this.gameState==GameState.FIGHT){
			teamRed.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
			teamBlue.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
			World world = this.arena.getWorld();
			for(Entity entity : world.getEntities()){
				if(entity instanceof Item){
					entity.remove();
				}
			}
			{
				//this will cause the red players to be evenly spreaded across all start positions
				Player player;
				float startPosition = 0;
				float increment = this.arena.getSpawnPositionCount("red")/(float)teamRed.getPlayerCount();
				for(UUID uuid : this.teamRed.getPlayers()){
					player = Bukkit.getPlayer(uuid);
					if(player==null) continue;
					player.teleport(this.arena.getSpawn(teamRed.getSide(),(int)startPosition));
					startPosition+=increment;
				}
			}
			{
				//this will cause the red players to be evenly spreaded across all start positions
				Player player;
				float startPosition = 0;
				float increment = this.arena.getSpawnPositionCount("red")/(float)teamBlue.getPlayerCount();
				for(UUID uuid : this.teamBlue.getPlayers()){
					player = Bukkit.getPlayer(uuid);
					if(player==null) continue;
					player.teleport(this.arena.getSpawn(teamBlue.getSide(),(int)startPosition));
					startPosition+=increment;
				}
			}
		}
		else if(this.gameState==GameState.FINISHED){
			teamRed.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
			teamBlue.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
			HandlerList.unregisterAll(eventListener);
		}
	}
	protected void updatePlayer(Player player){
		if(player==null) return;
		TowerControlTeam towerControlTeam = TowerControlTeam.get(player.getUniqueId());
		TowerControl.updateTabList(player, towerControlTeam);
		switch(this.gameState){
		case PREGAME:
			player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
			player.setGameMode(GameMode.ADVENTURE);
			player.updateInventory();
			TowerControl.resetScores(player.getName());
			break;
		case FIGHT:
			if(towerControlTeam==null){
				player.teleport(TowerControl.getCurrentArena().getWorld().getSpawnLocation());
				TowerControl.resetScores(player.getName());
				break;
			}
			player.setGameMode(GameMode.ADVENTURE);
			break;
		case FINISHED:
			player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
			player.setGameMode(GameMode.ADVENTURE);
			player.updateInventory();
			TowerControl.resetScores(player.getName());
			break;
		}
		sendGameState(player);
	}
	protected void updateInventory(Player player){
		if(this.gameState==GameState.FIGHT){
			PlayerInventory playerInventory = player.getInventory();
			playerInventory.clear();
			String materialString;
			for(ItemStack itemStack : this.arena.getInventoryTemplate()){
				materialString = itemStack.getType().toString().toLowerCase();
				if(materialString.contains("helmet")){
					playerInventory.setHelmet(itemStack);
				}
				else if(materialString.contains("chestplate")){
					playerInventory.setChestplate(itemStack);
				}
				else if(materialString.contains("leggings")){
					playerInventory.setLeggings(itemStack);
				}
				else if(materialString.contains("boots")){
					playerInventory.setBoots(itemStack);
				}
				else if(materialString.contains("shield")){
					playerInventory.setItemInOffHand(itemStack);
				}
				else{
					playerInventory.addItem(itemStack);
				}
			}
		}
		else{
			player.getInventory().clear();
		}
	}
	
	public void checkGameFinished(String side){
		this.arena.checkGameFinished(this, side);
	}
	
	protected boolean isFinished(){
		return this.gameState==GameState.FINISHED;
	}
	protected void sendGameState(Player player){
		TowerControlTeam team = TowerControlTeam.get(player.getUniqueId());
		switch(this.gameState){
		case PREGAME:
			player.sendMessage(ChatColor.DARK_AQUA+"Momentan läuft keine Partie.");
			if(team==null) player.sendMessage(ChatColor.GOLD+"Du kannst einem Team beitreten.");
			break;
		case FIGHT:
			player.sendMessage(ChatColor.DARK_AQUA+"Das Spiel läuft gerade.");
			if(team!=null){
				SwissSMPler.get(player).sendTitle("Kampf", ChatColor.GOLD+"Erobere die gegnerischen Türme!");
				player.sendMessage(ChatColor.GOLD+"Erobere die Türme der Gegner, bevor sie eure erklimmen!");
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
	protected Arena getArena(){
		return this.arena;
	}
	protected World getWorld(){
		return this.arena.getWorld();
	}
	protected TowerControlTeam getTeamRed(){
		return this.teamRed;
	}
	protected TowerControlTeam getTeamBlue(){
		return this.teamBlue;
	}
	protected String getMvpInfo(){
		String mvp = bestPlayer();
		Integer score = TowerControl.getScore(mvp).getScore();
		return ChatColor.YELLOW+"MVP: "+mvp+ChatColor.RESET+ChatColor.YELLOW+" ("+score+TowerControl.scoreSymbol+ChatColor.YELLOW+")";
	}
	protected void addScore(Player player, int score, String reason){
		if(player==null) return;
		Score s = TowerControl.getScore(player.getName());
		s.setScore(s.getScore()+score);
		SwissSMPler.get(player).sendActionBar(ChatColor.GREEN+"+"+score+TowerControl.scoreSymbol+" "+reason);
		player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 50, random.nextFloat()/0.2f+0.5f+score/1000f);
	}
	protected String bestPlayer() {
		HashMap<String, Integer> scores = new HashMap<String, Integer>();
	    for(Player player : Bukkit.getOnlinePlayers()) {
	        scores.put(player.getName(), TowerControl.getScore(player.getName()).getScore());
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

	protected static void applyGamerules(World world){
		world.setGameRuleValue("doMobSpawning", "false");
		world.setGameRuleValue("doDaylightCycle", "false");
		world.setGameRuleValue("doWeatherCycle", "false");
		world.setGameRuleValue("doFireTick", "true");
		world.setGameRuleValue("keepInventory", "true");
		world.setGameRuleValue("showDeathMessages", "false");
		world.setGameRuleValue("announceAdvancements", "false");
	}
}
