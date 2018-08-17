package ch.swisssmp.adventuredungeons.world;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

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
import ch.swisssmp.adventuredungeons.playermanagement.PlayerManager;
import ch.swisssmp.adventuredungeons.sound.MusicLoop;
import ch.swisssmp.transformations.AreaState;
import ch.swisssmp.transformations.TransformationArea;
import ch.swisssmp.transformations.TransformationWorld;
import ch.swisssmp.utils.RandomizedLocation;
import ch.swisssmp.utils.WorldUtil;

public class DungeonInstance{
	private final int instance_id;
	private final int dungeon_id;
	private final long seed;
	private World world;
	private final Difficulty difficulty;
	private final EventListenerMaster eventListener;
	private final PlayerManager playerManager;
	private boolean running = false;
	private final TransformationWorld transformationworld;
	
	private int respawnIndex = 0;

	public DungeonInstance(int dungeon_id, World world, Difficulty difficulty, int instance_id, long seed, ArrayList<String> player_uuids){
		this.dungeon_id = dungeon_id;
		this.instance_id = instance_id;
		this.seed = seed;
		for(String player_uuid : player_uuids){
			Dungeon.playerMap.put(player_uuid, this.instance_id);
		}
		
		this.world = world;
		this.difficulty = difficulty;
		this.eventListener = new EventListenerMaster(this);
		this.playerManager = new PlayerManager(this,player_uuids);
		this.transformationworld = TransformationWorld.get(world);
		this.transformationworld.loadTransformations("dungeon_template_"+this.dungeon_id);
		
		Dungeon.instances.put(this.instance_id, this);
		Dungeon.worldMap.put(this.world.getName(), this);
		
		if(Bukkit.getPluginManager().getPlugin("DungeonGenerator")!=null){
			DungeonGeneratorHandler.generateDungeons(world, "dungeon_template_"+dungeon_id, this.seed);
		}
	}
	
	//actions
	public void start(){
		this.running = true;
		Dungeon dungeon = Dungeon.get(this);
		if(dungeon==null) return;
		if(dungeon.lobby_trigger>0){
			TransformationArea transformation = transformationworld.getTransformation(dungeon.lobby_trigger);
			if(transformation!=null){
				AreaState areaState = transformation.getSchematic("Offen");
				if(areaState!=null) areaState.trigger();
			}
		}
		Bukkit.getPluginManager().callEvent(new DungeonStartEvent(this));
		this.playerManager.sendTitle(ChatColor.GREEN+"START!", dungeon.name);
		for(String player_uuid : this.playerManager.getPlayers()){
			Player player = Bukkit.getPlayer(UUID.fromString(player_uuid));
			if(player!=null) MusicLoop.update(player);
		}
	}
	
	public boolean setRespawnIndex(int respawnIndex){
		int oldIndex = this.respawnIndex;
		int newIndex = Math.min(this.getDungeon().respawnLocations.size()-1, Math.max(this.respawnIndex, respawnIndex));
		RandomizedLocation randomizedLocation = this.getDungeon().respawnLocations.get(newIndex);
		if(randomizedLocation!=null){
			this.respawnIndex = newIndex;
		}
		return newIndex==respawnIndex && newIndex!=oldIndex;
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
		RandomizedLocation randomizedRespawn = this.getDungeon().respawnLocations.get(this.respawnIndex);
		return randomizedRespawn.getLocation(this.getWorld());
	}
	
	public void delete(boolean graceful){
		Bukkit.getPluginManager().callEvent(new DungeonEndEvent(this));
		Dungeon dungeon = Dungeon.get(this.dungeon_id);
		World world = this.getWorld();
		Location location = dungeon.getLeavePoint();
		if(location==null){
			Bukkit.getLogger().info("LeavePoint is null! Falling back to global Spawnpoint");
			location = Bukkit.getWorlds().get(0).getSpawnLocation();
		}
		for(Player player : world.getPlayers()){
			player.teleport(location);
		}
		try{
			if(graceful) WorldUtil.deleteWorld(this.world, location, true);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		Dungeon.instances.remove(this.instance_id);
		this.eventListener.unregister();
	}
	public int getInstanceId() {
		return this.instance_id;
	}
}
