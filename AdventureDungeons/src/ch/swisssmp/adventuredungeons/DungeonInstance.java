package ch.swisssmp.adventuredungeons;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import ch.swisssmp.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import ch.swisssmp.adventuredungeons.event.DungeonEndEvent;
import ch.swisssmp.adventuredungeons.event.DungeonStartEvent;
import ch.swisssmp.adventuredungeons.event.listener.EventListenerMaster;
import ch.swisssmp.transformations.TransformationState;
import ch.swisssmp.transformations.AreaTransformation;
import ch.swisssmp.transformations.TransformationContainer;

public class DungeonInstance{
	private static HashMap<Integer,DungeonInstance> instances = new HashMap<Integer,DungeonInstance>();
	private static HashMap<World,DungeonInstance> worldMap = new HashMap<World,DungeonInstance>();
	
	private final int instance_id;
	private final int dungeon_id;
	private final long seed;
	private World world;
	private final Difficulty difficulty;
	private final EventListenerMaster eventListener;
	private final PlayerManager playerManager;
	private boolean running = false;
	private final TransformationContainer transformationworld;
	
	private String background_music;
	private long music_loop_time;

	private DungeonInstance(Dungeon dungeon, World world, Difficulty difficulty, int instance_id, long seed){
		this.dungeon_id = dungeon.getDungeonId();
		this.instance_id = instance_id;
		this.seed = seed;
		
		this.world = world;
		this.difficulty = difficulty;
		this.eventListener = new EventListenerMaster(this);
		this.playerManager = new PlayerManager(this);
		this.transformationworld = TransformationContainer.get(world);
		//this.transformationworld.loadTransformations("dungeon_template_"+this.dungeon_id);
		
		try{
			if(Bukkit.getPluginManager().getPlugin("DungeonGenerator")!=null){
				DungeonGeneratorHandler.generateDungeons(world, "dungeon_template_"+dungeon_id, this.seed);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String getBackgroundMusic(){
		return this.background_music;
	}
	
	public long getMusicLoopTime(){
		return this.music_loop_time;
	}
	
	//actions
	public void start(){
		this.running = true;
		Dungeon dungeon = Dungeon.get(this);
		if(dungeon==null) return;
		if(dungeon.getLobbyTrigger()>0){
			/*
			AreaTransformation transformation = transformationworld.getTransformation(dungeon.getLobbyTrigger());
			if(transformation!=null){
				TransformationState transformationState = transformation.getSchematic("Offen");
				if(transformationState !=null) transformationState.trigger();
			}*/
		}
		Bukkit.getPluginManager().callEvent(new DungeonStartEvent(this));
		this.playerManager.sendTitle(ChatColor.GREEN+"START!", dungeon.getName());
		for(String player_uuid : this.playerManager.getPlayers()){
			Player player = Bukkit.getPlayer(UUID.fromString(player_uuid));
			if(player!=null) MusicLoop.update(player);
		}
	}
	
	public boolean isRunning(){
		return this.running;
	}
	
	//getters
	public PlayerManager getPlayerManager(){
		return this.playerManager;
	}
	public File getWorldguardDirectory(){
		return new File(WorldGuardPlugin.inst().getDataFolder(), "worlds/"+getWorldName());
	}
	public File getWorldDirectory(){
		return new File(Bukkit.getWorldContainer(), this.getWorldName());
	}
	public String getWorldName(){
		return "dungeon_instance_"+this.instance_id;
	}
	public World getWorld(){
		return this.world;
	}
	public Dungeon getDungeon(){
		return Dungeon.get(this.dungeon_id);
	}
	public int getDungeonId(){
		return this.dungeon_id;
	}
	public Difficulty getDifficulty(){
		return this.difficulty;
	}
	public Location getRespawnLocation(){
		return this.world.getSpawnLocation();
	}
	public void delete(boolean graceful){
		Bukkit.getPluginManager().callEvent(new DungeonEndEvent(this));
		Dungeon dungeon = Dungeon.get(this.dungeon_id);
		World world = this.getWorld();
		Location location = dungeon.getLobbyLeave().getLocation(Bukkit.getWorlds().get(0));
		if(location==null){
			Bukkit.getLogger().info("[AdventureDungeons] LeavePoint is null! Falling back to global Spawnpoint");
			location = Bukkit.getWorlds().get(0).getSpawnLocation();
		}
		for(Player player : world.getPlayers()){
			player.teleport(location);
		}
		try{
			if(graceful){
				for(Player player : this.world.getPlayers()){
					player.teleport(location);
				}
				WorldManager.deleteWorld(this.world.getName());
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		instances.remove(this.instance_id);
		worldMap.remove(this.world);
		this.eventListener.unregister();
	}
	public int getInstanceId() {
		return this.instance_id;
	}
	
	protected static DungeonInstance create(int instance_id, Dungeon dungeon, Difficulty difficulty, World world, long seed){
		DungeonInstance result = new DungeonInstance(dungeon,world,difficulty,instance_id,seed);
		instances.put(instance_id, result);
		worldMap.put(world, result);
		return result;
	}
	
	public static DungeonInstance get(int instance_id){
		return instances.get(instance_id);
	}
	
	public static DungeonInstance get(World world){
		return worldMap.get(world);
	}
	
	public static DungeonInstance get(Player player){
		return DungeonInstance.get(player.getUniqueId());
	}
	
	public static DungeonInstance get(UUID player_uuid){
		String player_uuid_string = player_uuid.toString();
		for(DungeonInstance instance : instances.values()){
			if(instance.playerManager.getPlayers().contains(player_uuid_string)) return instance;
		}
		return null;
	}
	
	public static Collection<DungeonInstance> getAll(){
		return instances.values();
	}
}
