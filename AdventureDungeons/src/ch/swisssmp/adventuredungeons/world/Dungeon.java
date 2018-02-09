package ch.swisssmp.adventuredungeons.world;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.util.AdventureFileUtil;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.RandomizedLocation;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import net.md_5.bungee.api.ChatColor;

public class Dungeon{
	private static int auto_increment = 0;
	public static HashMap<Integer, Dungeon> dungeons = new HashMap<Integer, Dungeon>();
	public static HashMap<Integer, DungeonInstance> instances = new HashMap<Integer, DungeonInstance>();
	public static HashMap<String, DungeonInstance> worldMap = new HashMap<String, DungeonInstance>();
	public static File dungeonInstancesFile;
	public final static HashMap<String, Integer> playerMap = new HashMap<String, Integer>(); //String is a player_uuid, Integer is the instance_id of the Dungeon
	
	public final Integer dungeon_id;
	public final int background_music;
	public final Long looptime;
	public final Integer lobby_trigger;
	public final String name;
	public final int maxPlayers;
	public final HashMap<String, Vector> points = new HashMap<String, Vector>();
	
	public final RandomizedLocation lobby_join;
	public final RandomizedLocation lobby_leave;
	public final ArrayList<RandomizedLocation> respawnLocations;
	
	private final HashMap<String, String> gamerules;
	
	private Dungeon(ConfigurationSection dataSection){
		this.dungeon_id = dataSection.getInt("dungeon_id");
		this.name = dataSection.getString("name");
		this.background_music = dataSection.getInt("background_music");
		this.looptime = dataSection.getLong("looptime");
		this.lobby_trigger = dataSection.getInt("lobby_trigger");
		this.maxPlayers = dataSection.getInt("max_players");
		this.lobby_join = dataSection.getRandomizedLocation("lobby_join");
		this.lobby_leave = dataSection.getRandomizedLocation("lobby_leave");
		this.respawnLocations = new ArrayList<RandomizedLocation>();
		
		ConfigurationSection respawnsSection = dataSection.getConfigurationSection("respawns");
		for(String key : respawnsSection.getKeys(false)){
			respawnLocations.add(respawnsSection.getRandomizedLocation(key));
		}
		ConfigurationSection gamerulesSection = dataSection.getConfigurationSection("gamerules");
		this.gamerules = new HashMap<String,String>();
		if(gamerulesSection!=null){
			for(String key : gamerulesSection.getKeys(false)){
				this.gamerules.put(key, gamerulesSection.getString(key));
			}
		}
		AdventureDungeons.info("Dungeon "+this.name+" created");
	}
	
	public int getDungeonId(){
		return this.dungeon_id;
	}
	
	public void join(UUID player_uuid, DungeonInstance targetInstance, Difficulty difficulty){
		Player player = Bukkit.getPlayer(player_uuid);
		if(player==null)return;
		//check if the player is currently in a dungeon (and remove it there if it is)
		DungeonInstance currentInstance = getInstance(player_uuid);
		if(currentInstance!=null){
			if(currentInstance.getDungeonId()!=this.dungeon_id){
				Dungeon mmoDungeon = get(dungeon_id);
				mmoDungeon.leave(player_uuid);
			}
			else return;
		}
		player.setGameMode(GameMode.ADVENTURE);
		//no instance has been found, so lets attempt to create a new one
		if(targetInstance==null){
			targetInstance = createInstance(difficulty);
		}
		//if this is true there was an error generating an instance (which hopefully never happens!)
		if(targetInstance==null){
			player.sendMessage(ChatColor.RED+"Beim betreten des Dungeons ist ein Fehler aufgetreten. Bitte kontaktiere den Support.");
			return;
		}
		targetInstance.join(player);
	}
	
	public void leave(UUID player_uuid){
		DungeonInstance dungeonInstance = getInstance(player_uuid);
		if(dungeonInstance==null)return;
		dungeonInstance.leave(player_uuid);
	}
	
	public Location getLeavePoint(){
		if(this.lobby_leave!=null){
			return lobby_leave.getLocation();
		}
		else{
			World defaultWorld = Bukkit.getWorlds().get(0);
			return defaultWorld.getSpawnLocation();
		}
	}
	public World createTemplate(){
		String template_name = "dungeon_template_"+this.dungeon_id;
		if(Bukkit.getWorld(template_name)!=null){
			return Bukkit.getWorld(template_name);
		}
		WorldCreator templateCreator = new WorldCreator(template_name);
		templateCreator.generateStructures(false);
		templateCreator.type(WorldType.FLAT);
		World world = Bukkit.getServer().createWorld(templateCreator);
		applyGamerules(world, Difficulty.PEACEFUL);
		WorldGuardPlugin.inst().reloadConfig();
		AdventureDungeons.info("Created template for dungeon "+this.dungeon_id);
		return world;
	}
	public World editTemplate(){
		String template_name = "dungeon_template_"+this.dungeon_id;
		if(Bukkit.getWorld(template_name)!=null){
			return Bukkit.getWorld(template_name);
		}
		File source = new File(AdventureDungeons.plugin.getDataFolder(), "dungeon_templates/"+template_name);
		if(!source.exists() || !source.isDirectory()) return createTemplate();
		File target = new File(Bukkit.getWorldContainer(), template_name);
        ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
		if(!target.exists()){
			AdventureFileUtil.copyDirectory(source, target, ignore);
		}
		File mainWorldAdvancementsFile = new File(Bukkit.getWorldContainer(), Bukkit.getWorlds().get(0).getName()+"/data/advancements");
		File worldAdvancementsFile = new File(Bukkit.getWorldContainer(), template_name+"/data/advancements");
		if(worldAdvancementsFile.exists()){
			AdventureFileUtil.deleteRecursive(worldAdvancementsFile);
		}
		AdventureFileUtil.copyDirectory(mainWorldAdvancementsFile, worldAdvancementsFile);
		World world = Bukkit.getServer().createWorld(new WorldCreator(template_name));
		applyGamerules(world, Difficulty.PEACEFUL);
		WorldGuardPlugin.inst().reloadConfig();
		for(ProtectedRegion protectedRegion : WorldGuardPlugin.inst().getRegionManager(world).getRegions().values()){
			protectedRegion.setFlag(DefaultFlag.PASSTHROUGH, StateFlag.State.ALLOW);
		};
		AdventureDungeons.info("Retrieved template of dungeon "+this.dungeon_id);
		return world;
	}
	public boolean saveTemplate(){
		String template_name = "dungeon_template_"+this.dungeon_id;
		World world = Bukkit.getWorld(template_name);
		if(world==null){
			return false;
		}
		Location leavePoint = getLeavePoint();
		if(leavePoint!=null){
			for(Player player : world.getPlayers().toArray(new Player[world.getPlayers().size()])){
				player.teleport(leavePoint);
			}
		}
		//save the world one last time before unloading
		world.save();
		File source = new File(Bukkit.getWorldContainer(), template_name);
		if(!source.exists() || !source.isDirectory()) return false;
		File target = this.getTemplateDirectory();
		//task is run later because world first needs to finish saving
		Bukkit.getScheduler().runTaskLater(AdventureDungeons.plugin, new Runnable(){
			@Override
			public void run(){
				if(target.exists()){
					AdventureDungeons.info("Deleted old files of dungeon "+dungeon_id);
					AdventureFileUtil.deleteRecursive(target);
				}
		        ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
				AdventureFileUtil.copyDirectory(source, target, ignore);
				AdventureDungeons.info("Saved template of dungeon "+dungeon_id);
				AdventureWorldUtil.deleteWorld(world, leavePoint, false);
			}
		}, 20L);
		return true;
	}
	public DungeonInstance createInstance(Difficulty difficulty){
		int index = auto_increment;
		auto_increment++;
		File source = getTemplateDirectory();
		if(source.exists() && source.isDirectory()){
			String worldName = "dungeon_instance_"+index;
	        ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
			//copy world data
	        File worldTargetDirectory = new File(Bukkit.getWorldContainer(), worldName);
	        //strangely sometimes it fails to remove the old world file
	        if(worldTargetDirectory.exists()){
	        	AdventureFileUtil.deleteRecursive(worldTargetDirectory);
	        }
			AdventureFileUtil.copyDirectory(this.getTemplateDirectory(), worldTargetDirectory, ignore);
			//copy worldguard regions
			File worldGuardTargetDirectory = new File(WorldGuardPlugin.inst().getDataFolder(), "worlds/"+worldName);
			AdventureFileUtil.copyDirectory(this.getWorldguardDirectory(), worldGuardTargetDirectory, ignore);
			//copy advancement stuff because its stupid
			File mainWorldAdvancementsFile = new File(Bukkit.getWorldContainer(), Bukkit.getWorlds().get(0).getName()+"/data/advancements");
			File worldAdvancementsFile = new File(Bukkit.getWorldContainer(), worldName+"/data/advancements");
			if(worldAdvancementsFile.exists()){
				AdventureFileUtil.deleteRecursive(worldAdvancementsFile);
			}
			AdventureFileUtil.copyDirectory(mainWorldAdvancementsFile, worldAdvancementsFile);
			//let Spigot load the world
			World world = Bukkit.createWorld(new WorldCreator(worldName));
			applyGamerules(world, difficulty);
			if(world!=null){
				DungeonInstance dungeonInstance = new DungeonInstance(this.dungeon_id, world, difficulty, index, new ArrayList<String>());
				WorldGuardPlugin.inst().reloadConfig();
				for(ProtectedRegion protectedRegion : WorldGuardPlugin.inst().getRegionManager(world).getRegions().values()){
					protectedRegion.setFlag(DefaultFlag.PASSTHROUGH, StateFlag.State.ALLOW);
				};
				AdventureDungeons.info("Created instance of dungeon "+this.dungeon_id+" with instance id "+index);
				//from this point on everything is going fine
				return dungeonInstance;
			}
			else{
				return null;
			}
		}
		else return null;
	}
	private void applyGamerules(World world, Difficulty difficulty){
		world.setGameRuleValue("doMobSpawning", "false");
		world.setGameRuleValue("doDaylightCycle", "false");
		world.setGameRuleValue("doWeatherCycle", "false");
		world.setGameRuleValue("doFireTick", "false");
		world.setGameRuleValue("mobGriefing", "false");
		world.setGameRuleValue("keepInventory", "false");
		for(Entry<String,String> gamerule : this.gamerules.entrySet()){
			world.setGameRuleValue(gamerule.getKey(), gamerule.getValue());
		}
		world.setDifficulty(difficulty);
	}
	public String getWorldName(){
		return "dungeon_template_"+this.dungeon_id;
	}
	public File getTemplateDirectory(){
		return new File(AdventureDungeons.plugin.getDataFolder(), "dungeon_templates/"+getWorldName());
	}
	public File getWorldguardDirectory(){
		WorldGuardPlugin worldGuard = AdventureDungeons.worldGuardPlugin;
		return new File(worldGuard.getDataFolder(), "worlds/"+this.getWorldName());
	}
	public static DungeonInstance getInstance(Player player){
		if(player==null)return null;
		else return getInstance(player.getUniqueId());
	}
	public static Dungeon get(int mmo_dungeon_id){
		return dungeons.get(mmo_dungeon_id);
	}
	public static Dungeon get(Player player){
		DungeonInstance dungeonInstance = getInstance(player);
		return get(dungeonInstance);
	}
	public static DungeonInstance getInstance(Location location){
		if(location==null) return null;
		return worldMap.get(location.getWorld().getName());
	}
	public static DungeonInstance getInstance(UUID player_uuid){
		Integer instance_id = playerMap.get(player_uuid.toString());
		if(instance_id!=null){
			return getInstance(instance_id);
		}
		else return null;
	}
	public static DungeonInstance getInstance(int instance_id){
		return instances.get(instance_id);
	}
	public static DungeonInstance getInstance(String worldName){
		return worldMap.get(worldName);
	}
	public static Dungeon get(DungeonInstance dungeonInstance){
		if(dungeonInstance==null) return null;
		return get(dungeonInstance.getDungeonId());
	}
	//save and load stuff
	public static void loadDungeons(){
		dungeons = new HashMap<Integer, Dungeon>();
		
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("adventure/dungeons.php");
		if(yamlConfiguration==null){
			Bukkit.getLogger().info("[AdventureDungeons] Konnte Dungeons nicht laden.");
			return;
		}
		for(String key : yamlConfiguration.getKeys(false)){
			ConfigurationSection dataSection = yamlConfiguration.getConfigurationSection(key);
			dungeons.put(dataSection.getInt("dungeon_id"), new Dungeon(dataSection));
		}
	}
}
