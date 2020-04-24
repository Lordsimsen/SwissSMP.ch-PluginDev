package ch.swisssmp.towercontrol;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import ch.swisssmp.towercontrol.transformations.TransformationCommand;

public class TowerControl extends JavaPlugin{
	private Logger logger;
	protected static String scoreSymbol = ChatColor.RESET.toString();
	protected static TowerControl plugin;
	protected static File dataFolder;
	protected WorldEditPlugin worldEditPlugin;
	protected WorldGuardPlugin worldGuardPlugin;
	protected static Game game;
	protected static boolean debug = false;
	
	private Arena currentArena;
	private TowerControlTeam teamRed;
	private TowerControlTeam teamBlue;
	
	protected Scoreboard scoreboard;
	protected Objective objective;
	
	public void onEnable() {
		plugin = this;
		PluginDescriptionFile pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		dataFolder = this.getDataFolder();

		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		ConsoleCommand consoleCommand = new ConsoleCommand();
		this.getCommand("spiel").setExecutor(consoleCommand);
		this.getCommand("karte").setExecutor(consoleCommand);
		this.getCommand("transformation").setExecutor(new TransformationCommand());
		Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if(worldGuard instanceof WorldGuardPlugin){
			worldGuardPlugin = (WorldGuardPlugin) worldGuard;
		}
		else{
			throw new NullPointerException("WorldGuard missing");
		}
		Plugin worldEdit = Bukkit.getPluginManager().getPlugin("WorldEdit");
		if(worldEdit instanceof WorldEditPlugin){
			worldEditPlugin = (WorldEditPlugin) worldEdit;
		}
		else{
			throw new NullPointerException("WorldEdit missing");
		}
		World world = Bukkit.getWorlds().get(0);
		Game.applyGamerules(world);
		
		dataFolder = getDataFolder();
		for(Objective objective : Bukkit.getScoreboardManager().getMainScoreboard().getObjectives()){
			objective.unregister();
		}
		for(Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()){
			for(String entry : team.getEntries()){
				team.removeEntry(entry);
			}
			team.unregister();
		}
		this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		for(Objective objective : scoreboard.getObjectives()) objective.unregister();
		this.objective = scoreboard.registerNewObjective(TowerControl.scoreSymbol, "dummy");
		this.objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		TowerControlTeam.load("red", (team)->{
			if(team==null) return;
			this.teamRed = team;
			this.teamRed.register(this.scoreboard);
		});
		TowerControlTeam.load("blue", (team)->{
			if(team==null) return;
			this.teamBlue = team;
			this.teamBlue.register(this.scoreboard);
		});
		Arena.loadArenas();
	}
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
		HandlerList.unregisterAll(this);
	}
	protected void prepareGame(CommandSender host){
		if(this.currentArena==null){
			host.sendMessage("[TowerControl] Kann kein Spiel starten, da keine Karte aktiviert ist. '/karten aktiviere [Karten-ID]'");
			Bukkit.dispatchCommand(host, "map list");
			return;
		}
		if(this.teamRed==null){
			host.sendMessage("[TowerControl] Kann kein Spiel starten, da kein Team Rot vorhanden ist. (Fehler im System)");
			return;
		}
		if(this.teamBlue==null){
			host.sendMessage("[TowerControl] Kann kein Spiel starten, da kein Team Blau vorhanden ist. (Fehler im System)");
			return;
		}
		if(game!=null && game.getGameState()!=GameState.FINISHED) game.setFinished(null, null);
		game = new Game(this.currentArena, this.teamRed, this.teamBlue);
	}
	protected static void resetScores(String playerName){
		plugin.scoreboard.resetScores(playerName);
	}
	protected static Score getScore(String playerName){
		return plugin.objective.getScore(playerName);
	}
	protected static void setCurrentArena(Arena arena){
		plugin.currentArena = arena;
		if(game!=null) game.setArena(arena);
	}
	protected static Arena getCurrentArena(){
		return plugin.currentArena;
	}
	protected static TowerControlTeam getTeamRed(){
		return plugin.teamRed;
	}
	protected static TowerControlTeam getTeamBlue(){
		return plugin.teamBlue;
	}
	protected static void balanceTeams(){
		plugin.teamBlue.purgeDisconnected();
		plugin.teamRed.purgeDisconnected();
		int targetTeamSize = (plugin.teamBlue.getPlayerCount()+plugin.teamRed.getPlayerCount())/2;
		int playerDifference = Math.abs(plugin.teamBlue.getPlayerCount()-plugin.teamRed.getPlayerCount());
		if(playerDifference<=1){
			return;
		}
		TowerControlTeam smallerTeam;
		TowerControlTeam biggerTeam;
		if(plugin.teamBlue.getPlayerCount()>plugin.teamRed.getPlayerCount()){
			biggerTeam = plugin.teamBlue;
			smallerTeam = plugin.teamRed;
		}
		else{
			biggerTeam = plugin.teamRed;
			smallerTeam = plugin.teamBlue;
		}
		ArrayList<Player> autofilledPlayers = new ArrayList<Player>();
		Collection<UUID> currentPlayers = biggerTeam.getPlayers();
		Player currentPlayer;
		for(UUID player_uuid : currentPlayers){
			currentPlayer = Bukkit.getPlayer(player_uuid);
			if(currentPlayer==null)continue;
			autofilledPlayers.add(currentPlayer);
			if(autofilledPlayers.size()+smallerTeam.getPlayerCount()>=targetTeamSize)break;
		}
		for(Player player : autofilledPlayers){
			biggerTeam.leave(player.getUniqueId());
			smallerTeam.join(player);
			player.sendMessage("[TowerControl] "+ChatColor.GRAY+"Teams automatisch ausgeglichen.");
		}
	}
    protected static void updateTabList(Player player, TowerControlTeam team){
    	String fullDisplayName;
    	if(team!=null){
    		fullDisplayName = team.getColor()+player.getName()+ChatColor.RESET;
    	}
    	else{
    		fullDisplayName = player.getName()+ChatColor.RESET;
    	}
		String header = ChatColor.RED+"SwissSMP.ch";
		String footer = ChatColor.GRAY+"Tower Control Event";
		player.setDisplayName(fullDisplayName);
		player.setPlayerListName(fullDisplayName);
		TabList.setHeaderFooter(player, header, footer);
    }
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(/*Collections.reverseOrder()*/))
                .collect(Collectors.toMap(
                  Map.Entry::getKey, 
                  Map.Entry::getValue, 
                  (e1, e2) -> e1, 
                  LinkedHashMap::new
                		));
    }
    public WorldEditPlugin getWorldEdit(){
    	return this.worldEditPlugin;
    }
    public WorldGuardPlugin getWorldGuard(){
    	return this.worldGuardPlugin;
    }
    public static TowerControl getPlugin(){
    	return plugin;
    }
    protected static Collection<UUID> getPlayers(){
    	return TowerControlTeam.getAllPlayers();
    }
}
