package ch.swisssmp.adventuredungeons.world;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.util.MmoFileUtil;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.RandomizedLocation;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import net.md_5.bungee.api.ChatColor;

public class Dungeon implements Listener{
	private static int auto_increment = 0;
	public static HashMap<Integer, Dungeon> dungeons = new HashMap<Integer, Dungeon>();
	public static HashMap<Integer, DungeonInstance> instances = new HashMap<Integer, DungeonInstance>();
	public static HashMap<String, DungeonInstance> worldMap = new HashMap<String, DungeonInstance>();
	public static File dungeonInstancesFile;
	public final static HashMap<String, Integer> playerMap = new HashMap<String, Integer>(); //String is a player_uuid, Integer is the instance_id of the Dungeon
	
	public final Integer mmo_dungeon_id;
	public final int background_music;
	public final Long looptime;
	public final Integer lobby_trigger;
	public final String name;
	public final int maxPlayers;
	public final HashMap<Integer, String> transformations = new HashMap<Integer, String>();
	public final HashMap<String, Vector> points = new HashMap<String, Vector>();
	
	public final RandomizedLocation lobby_join;
	public final RandomizedLocation lobby_leave;
	public final ArrayList<RandomizedLocation> respawnLocations;
	
	public Dungeon(ConfigurationSection dataSection){
		this.mmo_dungeon_id = dataSection.getInt("mmo_dungeon_id");
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
		ConfigurationSection transformationsSection = dataSection.getConfigurationSection("transformations");
		if(transformationsSection!=null){
			for(String key : transformationsSection.getKeys(false)){
				ConfigurationSection transformationSection = transformationsSection.getConfigurationSection(key);
				int mmo_multistatearea_id = transformationSection.getInt("mmo_multistatearea_id");
				String transformation = transformationSection.getString("transformation");
				if(transformation!=null && !transformation.equals("")){
					this.transformations.put(mmo_multistatearea_id, transformation);
				}
			}
		}
		dungeons.put(mmo_dungeon_id, this);
		AdventureDungeons.info("Dungeon "+this.name+" created");
	}
	
	public void join(UUID player_uuid, DungeonInstance targetInstance){
		Player player = Bukkit.getPlayer(player_uuid);
		if(player==null)return;
		player.setGameMode(GameMode.ADVENTURE);
		//check if the player is currently in a dungeon (and remove it there if it is)
		DungeonInstance currentInstance = getInstance(player_uuid);
		if(currentInstance!=null){
			if(currentInstance.dungeon_id!=this.mmo_dungeon_id){
				Dungeon mmoDungeon = get(mmo_dungeon_id);
				mmoDungeon.leave(player_uuid);
			}
			else return;
		}
		//no instance has been found, so lets attempt to create a new one
		if(targetInstance==null){
			targetInstance = createInstance();
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
			World defaultWorld = Bukkit.getWorld(AdventureDungeons.config.getString("default_world"));
			return defaultWorld.getSpawnLocation();
		}
	}
	public World createTemplate(){
		String template_name = "dungeon_template_"+this.mmo_dungeon_id;
		if(Bukkit.getWorld(template_name)!=null){
			return Bukkit.getWorld(template_name);
		}
		WorldCreator templateCreator = new WorldCreator(template_name);
		templateCreator.generateStructures(false);
		templateCreator.type(WorldType.FLAT);
		World world = Bukkit.getServer().createWorld(templateCreator);
		applyGamerules(world);
		new AdventureWorldInstance(-1, template_name, world, AdventureWorldType.DUNGEON_TEMPLATE);
		AdventureDungeons.info("Created template for dungeon "+this.mmo_dungeon_id);
		return world;
	}
	public World editTemplate(){
		String template_name = "dungeon_template_"+this.mmo_dungeon_id;
		if(Bukkit.getWorld(template_name)!=null){
			return Bukkit.getWorld(template_name);
		}
		File source = new File(AdventureDungeons.dataFolder, "dungeon_templates/"+template_name);
		if(source.exists() && source.isDirectory()){
			File target = new File(Bukkit.getWorldContainer(), template_name);
			if(!target.exists()){
				AdventureWorld.copyDirectory(source, target);
			}
			World world = Bukkit.getServer().createWorld(new WorldCreator(template_name));
			applyGamerules(world);
			new AdventureWorldInstance(-1, template_name, world, AdventureWorldType.DUNGEON_TEMPLATE);
			AdventureDungeons.info("Retrieved template of dungeon "+this.mmo_dungeon_id);
			return world;
		}
		else return createTemplate();
	}
	public boolean saveTemplate(){
		String template_name = "dungeon_template_"+this.mmo_dungeon_id;
		World world = Bukkit.getWorld(template_name);
		if(world==null){
			return false;
		}
		world.save();
		Location leavePoint = getLeavePoint();
		AdventureWorldInstance worldInstance = AdventureWorld.getInstance(template_name);
		if(worldInstance==null) return false;
		File source = new File(Bukkit.getWorldContainer(), template_name);
		if(source.exists() && source.isDirectory()){
			File target = this.getTemplateDirectory();
			Bukkit.getScheduler().runTaskLater(AdventureDungeons.plugin, new Runnable(){
				@Override
				public void run(){
					if(target.exists()){
						AdventureDungeons.info("Deleted old files of dungeon "+mmo_dungeon_id);
						MmoFileUtil.deleteRecursive(target);
					}
					AdventureWorld.copyDirectory(source, target);
					AdventureDungeons.info("Saved template of dungeon "+mmo_dungeon_id);
					worldInstance.delete(leavePoint, false);
				}
			}, 20L);
			return true;
		}
		else return false;
	}
	public DungeonInstance createInstance(){
		int index = auto_increment;
		auto_increment++;
		File source = getTemplateDirectory();
		if(source.exists() && source.isDirectory()){
			//copy world data
			DungeonInstance dungeonInstance = new DungeonInstance(this.mmo_dungeon_id, index, new ArrayList<String>());
			World world = Bukkit.createWorld(new WorldCreator(dungeonInstance.getWorldName()));
			applyGamerules(world);
			new AdventureWorldInstance(-1, this.getWorldName(), world, AdventureWorldType.DUNGEON_INSTANCE);
			AdventureDungeons.info("Created instance of dungeon "+this.mmo_dungeon_id+" with instance id "+index);
			//from this point on everything is going fine
			return dungeonInstance;
		}
		else return null;
	}
	private void applyGamerules(World world){
		world.setGameRuleValue("doMobSpawning", "false");
		world.setGameRuleValue("doDaylightCycle", "false");
		world.setGameRuleValue("doWeatherCycle", "false");
		world.setGameRuleValue("doFireTick", "false");
		world.setGameRuleValue("doMobGriefing", "false");
	}
	public void deleteInstance(int instance_id){
		DungeonInstance dungeonInstance = getInstance(instance_id);
		dungeonInstance.delete();
	}
	public String getWorldName(){
		return "dungeon_template_"+this.mmo_dungeon_id;
	}
	public File getTemplateDirectory(){
		return new File(AdventureDungeons.dataFolder, "dungeon_templates/"+getWorldName());
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
		return worldMap.get(location.getWorld());
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
		if(worldName==null) return null;
		if(!worldName.contains("dungeon_instance_")) return null;
		try{
			int instance_id = Integer.parseInt(worldName.split("_")[2]);
			return instances.get(instance_id);
		}
		catch(Exception e){
			return null;
		}
	}
	public static Dungeon get(DungeonInstance dungeonInstance){
		if(dungeonInstance==null) return null;
		return get(dungeonInstance.dungeon_id);
	}
	//save and load stuff
	public static void loadDungeons(boolean fullload) throws Exception{
		for(Dungeon dungeon : dungeons.values()){
			HandlerList.unregisterAll(dungeon);
		}
		dungeons = new HashMap<Integer, Dungeon>();
		
		YamlConfiguration mmoDungeonsConfiguration = DataSource.getYamlResponse("adventure/dungeons.php");
		for(String dungeonIDstring : mmoDungeonsConfiguration.getKeys(false)){
			ConfigurationSection dataSection = mmoDungeonsConfiguration.getConfigurationSection(dungeonIDstring);
			new Dungeon(dataSection);
		}

		dungeonInstancesFile = new File(AdventureDungeons.dataFolder, "dungeons.yml");
		if(dungeonInstancesFile.exists()){
			YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(dungeonInstancesFile);
			auto_increment = yamlConfiguration.getInt("auto_increment");
			if(yamlConfiguration.contains("dungeon_instances")){
				ConfigurationSection dungeonsSection = yamlConfiguration.getConfigurationSection("dungeon_instances");
				for(String key : dungeonsSection.getKeys(false)){
					DungeonInstance.load(dungeonsSection.getConfigurationSection(key));
				}
			}
		}
	}
	public static void saveDungeons(){
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		yamlConfiguration.set("auto_increment", auto_increment);
		ConfigurationSection dungeon_instancesSection = yamlConfiguration.createSection("dungeon_instances");
		for(DungeonInstance dungeonInstance : instances.values()){
			dungeonInstance.save(dungeon_instancesSection.createSection(dungeonInstance.getWorldName()));
		}
		yamlConfiguration.save(dungeonInstancesFile);
	}
}
