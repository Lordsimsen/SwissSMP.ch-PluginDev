package ch.swisssmp.adventuredungeons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.Position;
import ch.swisssmp.utils.Random;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.world.WorldManager;
import ch.swisssmp.world.WorldTransferObserver;

public class Dungeon{
	private static Random random = new Random();
	private static int auto_increment = 0;
	private static HashMap<Integer, Dungeon> dungeons = new HashMap<Integer, Dungeon>();
	
	private final Integer dungeon_id;
	private final String name;
	private final int maxPlayers;
	private final String background_music;
	private final Long music_loop_time;
	private final Integer lobby_trigger;
	
	private final Position lobby_join;
	private final Position lobby_leave;
	private final ArrayList<Position> respawn_points;
	
	private final HashMap<String, String> gamerules;
	
	private Dungeon(ConfigurationSection dataSection){
		this.dungeon_id = dataSection.getInt("dungeon_id");
		this.name = dataSection.getString("name");
		this.maxPlayers = dataSection.getInt("max_players");
		this.background_music = dataSection.getString("background_music") != null ? dataSection.getString("background_music") : "";
		this.music_loop_time = dataSection.getLong("looptime");
		this.lobby_trigger = dataSection.getInt("lobby_trigger");
		this.lobby_join = dataSection.getPosition("lobby_join");
		this.lobby_leave = dataSection.getPosition("lobby_leave");
		this.respawn_points = new ArrayList<Position>();
		
		ConfigurationSection respawnsSection = dataSection.getConfigurationSection("respawns");
		for(String key : respawnsSection.getKeys(false)){
			respawn_points.add(respawnsSection.getPosition(key));
		}
		ConfigurationSection gamerulesSection = dataSection.getConfigurationSection("gamerules");
		this.gamerules = new HashMap<String,String>();
		if(gamerulesSection!=null){
			for(String key : gamerulesSection.getKeys(false)){
				this.gamerules.put(key, gamerulesSection.getString(key));
			}
		}
	}
	
	public int getDungeonId(){
		return this.dungeon_id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getMaxPlayers(){
		return this.maxPlayers;
	}
	
	public String getBackgroundMusic(){
		return this.background_music;
	}
	
	public long getMusicLoopTime(){
		return this.music_loop_time;
	}
	
	public ArrayList<Position> getRespawnPoints(){
		return new ArrayList<Position>(this.respawn_points);
	}
	
	public void join(Player player, DungeonInstance targetInstance, Difficulty difficulty){
		if(player==null)return;
		//check if the player is currently in a dungeon (and remove them there if they are)
		DungeonInstance currentInstance = DungeonInstance.get(player);
		if(currentInstance!=null){
			if(currentInstance.getDungeonId()!=this.dungeon_id){
				currentInstance.getPlayerManager().leave(player.getUniqueId());
			}
			else return;
		}
		if(targetInstance!=null){
			targetInstance.getPlayerManager().join(player);
		}
		else{
			//no instance has been found, so lets attempt to create a new one
			initiateInstance(player, difficulty);
		}
	}
	
	public void leave(UUID player_uuid){
		DungeonInstance dungeonInstance = DungeonInstance.get(player_uuid);
		if(dungeonInstance==null)return;
		dungeonInstance.getPlayerManager().leave(player_uuid);
	}
	
	public Position getLobbyJoin(){
		return this.lobby_join;
	}
	
	public Position getLobbyLeave(){
		if(this.lobby_leave!=null){
			return this.lobby_leave;
		}
		else{
			World defaultWorld = Bukkit.getWorlds().get(0);
			return new Position(defaultWorld.getSpawnLocation());
		}
	}
	
	public int getLobbyTrigger(){
		return this.lobby_trigger;
	}
	
	public World createTemplate(){
		String template_name = this.getTemplateName();
		if(Bukkit.getWorld(template_name)!=null){
			return Bukkit.getWorld(template_name);
		}
		WorldCreator templateCreator = new WorldCreator(template_name);
		templateCreator.generateStructures(false);
		templateCreator.type(WorldType.FLAT);
		World world = Bukkit.getServer().createWorld(templateCreator);
		this.applyGamerules(world, Difficulty.PEACEFUL, false);
		WorldGuardPlugin.inst().reloadConfig();
		for(ProtectedRegion protectedRegion : WorldGuardPlugin.inst().getRegionManager(world).getRegions().values()){
			protectedRegion.setFlag(DefaultFlag.PASSTHROUGH, StateFlag.State.ALLOW);
		};
		return world;
	}
	
	public void initiateEditor(Player player){
		String template_name = this.getTemplateName();
		World world = Bukkit.getWorld(template_name);
		if(world==null){
			player.teleport(this.lobby_join.getLocation(world));
			if(WorldManager.localWorldExists(template_name)){
				//World is present on local disk, load it and then teleport into it
				world = WorldManager.loadWorld(template_name);
			}
			else if(WorldManager.remoteWorldExists(template_name)){
				//World is present on remote disk, download it, load it, then teleport into it
				WorldTransferObserver observer = WorldManager.downloadWorld(player, template_name);
				observer.addOnFinishListener(new Runnable(){
					public void run(){
						initiateEditor(player);
					}
				});
				return;
			}
			else{
				//World does not exist, create it then teleport into it
				world = this.createTemplate();
			}
		}
		if(world!=null){
			//World is loaded, teleport into it
			player.teleport(this.lobby_join.getLocation(world));
		}
		else{
			player.sendMessage("[AdventureDungeons] Konnte den Editor nicht initiieren.");
		}
	}
	
	public boolean saveTemplate(CommandSender sender){
		String template_name = this.getTemplateName();
		World world = Bukkit.getWorld(template_name);
		if(world==null){
			return false;
		}
		Location leavePoint = this.lobby_leave.getLocation(Bukkit.getWorlds().get(0));
		if(leavePoint!=null){
			for(Player player : world.getPlayers().toArray(new Player[world.getPlayers().size()])){
				player.teleport(leavePoint);
			}
		}
		//save the world one last time before unloading
		world.save();
		Bukkit.getScheduler().runTaskLater(AdventureDungeons.getInstance(), new Runnable(){
			public void run(){
				WorldTransferObserver transferObserver = WorldManager.uploadWorld(sender, template_name);
				transferObserver.addOnFinishListener(new Runnable(){
					public void run(){
						Bukkit.getLogger().info("[AdventureDungeons] Delete World "+world.getName()+" now.");
						//FileUtil.deleteRecursive(world.getWorldFolder());
					}
				});
			}
		}, 100L);
		return true;
	}
	
	public void initiateInstance(Player player, Difficulty difficulty){
		int instance_id = auto_increment;
		auto_increment++;
		this.initiateInstance(instance_id, player, difficulty);
	}
	
	private void initiateInstance(int instance_id, Player player, Difficulty difficulty){
		String worldName = "dungeon_instance_"+instance_id;
		World world = Bukkit.getWorld(worldName);
		if(world==null){
			if(WorldManager.localWorldExists(worldName)){
				world = WorldManager.loadWorld(worldName);
			}
			else if(WorldManager.remoteWorldExists(worldName)){
				//World is present on remote disk, download it, load it, then teleport into it
				WorldTransferObserver observer = WorldManager.downloadWorld(player, this.getTemplateName(), worldName);
				observer.addOnFinishListener(new Runnable(){
					public void run(){
						initiateInstance(instance_id, player, difficulty);
					}
				});
				return;
			}
			else{
				player.sendMessage("["+this.name+ChatColor.RESET+"] "+ChatColor.RED+"Dieser Dungeon ist noch nicht eingerichtet.");
			}
		}
		if(world==null){
			player.sendMessage("["+this.name+ChatColor.RESET+"] "+ChatColor.RED+"Konnte die Instanz nicht öffnen. Bitte kontaktiere die Spielleitung.");
			return;
		}
		WorldGuardPlugin.inst().reloadConfig();
		for(ProtectedRegion protectedRegion : WorldGuardPlugin.inst().getRegionManager(world).getRegions().values()){
			protectedRegion.setFlag(DefaultFlag.PASSTHROUGH, StateFlag.State.ALLOW);
		};
		this.applyGamerules(world, difficulty, true);
		DungeonInstance dungeonInstance = DungeonInstance.create(instance_id, this, difficulty, world, random.nextLong());
		dungeonInstance.getPlayerManager().join(player);
	}
	
	private void applyGamerules(World world, Difficulty difficulty, boolean isInstance){
		world.setGameRuleValue("doMobSpawning", "false");
		world.setGameRuleValue("doDaylightCycle", "false");
		world.setGameRuleValue("doWeatherCycle", "false");
		world.setGameRuleValue("doFireTick", "false");
		world.setGameRuleValue("mobGriefing", "false");
		world.setGameRuleValue("keepInventory", "false");
		for(Entry<String,String> gamerule : this.gamerules.entrySet()){
			world.setGameRuleValue(gamerule.getKey(), gamerule.getValue());
		}
		if(isInstance){
			world.setGameRuleValue("doMobCampSpawning", "true");
		}
		world.setDifficulty(difficulty);
	}
	
	public String getTemplateName(){
		return "dungeon_template_"+this.dungeon_id;
	}
	
	public static Dungeon get(int dungeon_id){
		if(dungeons.containsKey(dungeon_id)) return dungeons.get(dungeon_id);
		return Dungeon.load(dungeon_id);
	}
	
	public static Dungeon get(DungeonInstance dungeonInstance){
		if(dungeonInstance==null) return null;
		return Dungeon.get(dungeonInstance.getDungeonId());
	}

	private static Dungeon load(int dungeon_id){
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("dungeons/get_dungeon.php");
		if(yamlConfiguration==null || !yamlConfiguration.contains("dungeon")){
			Bukkit.getLogger().info("[AdventureDungeons] Konnte Dungeon "+dungeon_id+" nicht laden.");
			return null;
		}
		ConfigurationSection dataSection = yamlConfiguration.getConfigurationSection("dungeon");
		Dungeon result = new Dungeon(dataSection);
		dungeons.put(result.getDungeonId(), result);
		return result;
	}
}
