package ch.swisssmp.adventuredungeons.mmoworld;

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

import ch.swisssmp.adventuredungeons.Main;
import ch.swisssmp.adventuredungeons.mmoevent.MmoEvent;
import ch.swisssmp.adventuredungeons.mmoevent.MmoEventType;
import ch.swisssmp.adventuredungeons.util.MmoDelayedThreadTask;
import ch.swisssmp.adventuredungeons.util.MmoFileUtil;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import net.md_5.bungee.api.ChatColor;

public class MmoDungeon implements Listener{
	private static int auto_increment = 0;
	public static HashMap<Integer, MmoDungeon> dungeons = new HashMap<Integer, MmoDungeon>();
	public static HashMap<Integer, MmoDungeonInstance> instances = new HashMap<Integer, MmoDungeonInstance>();
	public static HashMap<String, MmoDungeonInstance> worldMap = new HashMap<String, MmoDungeonInstance>();
	public static File dungeonInstancesFile;
	public final static HashMap<String, Integer> playerMap = new HashMap<String, Integer>(); //String is a player_uuid, Integer is the instance_id of the Dungeon
	
	public final Integer mmo_dungeon_id;
	public final int background_music;
	public final Long looptime;
	public final Integer lobby_trigger;
	public final String name;
	public final int maxPlayers;
	public final HashMap<Integer, String> respawn_points = new HashMap<Integer, String>();
	public final HashMap<Integer, String> transformations = new HashMap<Integer, String>();
	public final HashMap<String, Vector> points = new HashMap<String, Vector>();
	public final HashMap<MmoEventType, MmoEvent> events = new HashMap<MmoEventType, MmoEvent>();
	
	public Vector lobby_join = null;
	public Location lobby_leave = null;
	public Vector respawn = null;
	
	public MmoDungeon(ConfigurationSection dataSection){
		this.mmo_dungeon_id = dataSection.getInt("mmo_dungeon_id");
		this.name = dataSection.getString("name");
		this.background_music = dataSection.getInt("background_music");
		this.looptime = dataSection.getLong("looptime");
		this.lobby_trigger = dataSection.getInt("lobby_trigger");
		ConfigurationSection configurationSection = dataSection.getConfigurationSection("configuration");
		this.maxPlayers = configurationSection.getInt("max_players");
		ConfigurationSection pointsSection = dataSection.getConfigurationSection("points");
		if(pointsSection!=null){
			for(String key : pointsSection.getKeys(false)){
				ConfigurationSection pointSection = pointsSection.getConfigurationSection(key);
				int x = pointSection.getInt("x");
				int y = pointSection.getInt("y");
				int z = pointSection.getInt("z");
				Vector vector = new Vector(x, y, z);
				String name = pointSection.getString("name");
				String worldName = pointSection.getString("world");
				World world = null;
				if(worldName!=null){
					world = Bukkit.getWorld(worldName);
				}
				points.put(name, vector);
				String point_type = pointSection.getString("type");
				switch(point_type){
				case "lobby_join":
					this.lobby_join = vector;
					break;
				case "lobby_leave":
					if(world==null) world = Bukkit.getWorld(Main.config.getString("default_world"));
					this.lobby_leave = new Location(world, vector.getX(), vector.getY(), vector.getZ());
					break;
				case "respawn":
					this.respawn = vector;
					break;
				default:
					break;
				}
			}
		}
		ConfigurationSection regionsSection = dataSection.getConfigurationSection("regions");
		if(regionsSection!=null){
			for(String key : regionsSection.getKeys(false)){
				ConfigurationSection regionSection = regionsSection.getConfigurationSection(key);
				int mmo_region_id = regionSection.getInt("mmo_region_id");
				String respawn_point = regionSection.getString("respawn_point");
				if(respawn_point!=null && !respawn_point.equals("")){
					this.respawn_points.put(mmo_region_id, respawn_point);
				}
			}
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
		MmoEvent.registerAll(dataSection, events);
		dungeons.put(mmo_dungeon_id, this);
		Main.info("Dungeon "+this.name+" created");
	}
	
	public void join(UUID player_uuid, MmoDungeonInstance targetInstance){
		Player player = Bukkit.getPlayer(player_uuid);
		if(player==null)return;
		player.setGameMode(GameMode.ADVENTURE);
		//check if the player is currently in a dungeon (and remove it there if it is)
		MmoDungeonInstance currentInstance = getInstance(player_uuid);
		if(currentInstance!=null){
			if(currentInstance.mmo_dungeon_id!=this.mmo_dungeon_id){
				MmoDungeon mmoDungeon = get(mmo_dungeon_id);
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
		MmoDungeonInstance dungeonInstance = getInstance(player_uuid);
		if(dungeonInstance==null)return;
		dungeonInstance.leave(player_uuid);
	}
	
	public Location getLeavePoint(){
		if(this.lobby_leave!=null){
			return new Location(this.lobby_leave.getWorld(), this.lobby_leave.getX()+0.5, this.lobby_leave.getY()+0.5, this.lobby_leave.getZ()+0.5);
		}
		else{
			World defaultWorld = Bukkit.getWorld(Main.config.getString("default_world"));
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
		new MmoWorldInstance(-1, template_name, world, MmoWorldType.DUNGEON_TEMPLATE);
		Main.info("Created template for dungeon "+this.mmo_dungeon_id);
		return world;
	}
	public World editTemplate(){
		String template_name = "dungeon_template_"+this.mmo_dungeon_id;
		if(Bukkit.getWorld(template_name)!=null){
			return Bukkit.getWorld(template_name);
		}
		File source = new File(Main.dataFolder, "dungeon_templates/"+template_name);
		if(source.exists() && source.isDirectory()){
			File target = new File(Bukkit.getWorldContainer(), template_name);
			if(!target.exists()){
				MmoWorld.copyDirectory(source, target);
			}
			World world = Bukkit.getServer().createWorld(new WorldCreator(template_name));
			applyGamerules(world);
			new MmoWorldInstance(-1, template_name, world, MmoWorldType.DUNGEON_TEMPLATE);
			Main.info("Retrieved template of dungeon "+this.mmo_dungeon_id);
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
		MmoWorldInstance worldInstance = MmoWorld.getInstance(template_name);
		if(worldInstance==null) return false;
		File source = new File(Bukkit.getWorldContainer(), template_name);
		if(source.exists() && source.isDirectory()){
			File target = this.getTemplateDirectory();
			Runnable task = new MmoDelayedThreadTask(new Runnable(){
				@Override
				public void run(){
					if(target.exists()){
						Main.info("Deleted old files of dungeon "+mmo_dungeon_id);
						MmoFileUtil.deleteRecursive(target);
					}
					MmoWorld.copyDirectory(source, target);
					Main.info("Saved template of dungeon "+mmo_dungeon_id);
					worldInstance.delete(leavePoint, false);
				}
			});
			Bukkit.getScheduler().runTaskLater(Main.plugin, task, 20L);
			return true;
		}
		else return false;
	}
	public MmoDungeonInstance createInstance(){
		int index = auto_increment;
		auto_increment++;
		File source = getTemplateDirectory();
		if(source.exists() && source.isDirectory()){
			//copy world data
			MmoDungeonInstance dungeonInstance = new MmoDungeonInstance(this.mmo_dungeon_id, index, new ArrayList<String>());
			World world = Bukkit.createWorld(new WorldCreator(dungeonInstance.getWorldName()));
			applyGamerules(world);
			new MmoWorldInstance(-1, this.getWorldName(), world, MmoWorldType.DUNGEON_INSTANCE);
			Main.info("Created instance of dungeon "+this.mmo_dungeon_id+" with instance id "+index);
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
		MmoDungeonInstance dungeonInstance = getInstance(instance_id);
		dungeonInstance.delete();
	}
	public String getWorldName(){
		return "dungeon_template_"+this.mmo_dungeon_id;
	}
	public File getTemplateDirectory(){
		return new File(Main.dataFolder, "dungeon_templates/"+getWorldName());
	}
	public File getWorldguardDirectory(){
		WorldGuardPlugin worldGuard = Main.worldGuardPlugin;
		return new File(worldGuard.getDataFolder(), "worlds/"+this.getWorldName());
	}
	public static MmoDungeonInstance getInstance(Player player){
		if(player==null)return null;
		else return getInstance(player.getUniqueId());
	}
	public static MmoDungeon get(int mmo_dungeon_id){
		return dungeons.get(mmo_dungeon_id);
	}
	public static MmoDungeon get(Player player){
		MmoDungeonInstance dungeonInstance = getInstance(player);
		return get(dungeonInstance);
	}
	public static MmoDungeonInstance getInstance(Location location){
		if(location==null) return null;
		return worldMap.get(location.getWorld());
	}
	public static MmoDungeonInstance getInstance(UUID player_uuid){
		Integer instance_id = playerMap.get(player_uuid.toString());
		if(instance_id!=null){
			return getInstance(instance_id);
		}
		else return null;
	}
	public static MmoDungeonInstance getInstance(int instance_id){
		return instances.get(instance_id);
	}
	public static MmoDungeon get(MmoDungeonInstance dungeonInstance){
		if(dungeonInstance==null) return null;
		return get(dungeonInstance.mmo_dungeon_id);
	}
	//save and load stuff
	public synchronized static void loadDungeons(boolean fullload) throws Exception{
		for(MmoDungeon dungeon : dungeons.values()){
			HandlerList.unregisterAll(dungeon);
		}
		dungeons = new HashMap<Integer, MmoDungeon>();
		
		YamlConfiguration mmoDungeonsConfiguration = DataSource.getYamlResponse("dungeons.php");
		for(String dungeonIDstring : mmoDungeonsConfiguration.getKeys(false)){
			ConfigurationSection dataSection = mmoDungeonsConfiguration.getConfigurationSection(dungeonIDstring);
			new MmoDungeon(dataSection);
		}

		dungeonInstancesFile = new File(Main.dataFolder, "dungeons.yml");
		if(dungeonInstancesFile.exists()){
			YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(dungeonInstancesFile);
			auto_increment = yamlConfiguration.getInt("auto_increment");
			ConfigurationSection dungeonsSection = yamlConfiguration.getConfigurationSection("dungeon_instances");
			for(String key : dungeonsSection.getKeys(false)){
				MmoDungeonInstance.load(dungeonsSection.getConfigurationSection(key));
			}
		}
	}
	public static void saveDungeons(){
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		yamlConfiguration.set("auto_increment", auto_increment);
		ConfigurationSection dungeon_instancesSection = yamlConfiguration.createSection("dungeon_instances");
		for(MmoDungeonInstance dungeonInstance : instances.values()){
			dungeonInstance.save(dungeon_instancesSection.createSection(dungeonInstance.getWorldName()));
		}
		yamlConfiguration.save(dungeonInstancesFile);
	}
}
