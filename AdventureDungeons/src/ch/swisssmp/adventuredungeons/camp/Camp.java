package ch.swisssmp.adventuredungeons.camp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.event.CampTriggerEvent;
import ch.swisssmp.adventuredungeons.world.DungeonInstance;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class Camp {
	private final World world;
	private final int dungeon_id;
	private final int instance_id;
	private final Integer camp_id;
	private final List<Vector> spawnpoints;
	private final HashMap<EntityType,Integer> mobs;
	private final List<Entity>
	
	public Camp(DungeonInstance dungeonInstance, ConfigurationSection dataSection){
		this.world = dungeonInstance.getWorld();
		this.dungeon_id = dungeonInstance.dungeon_id;
		this.instance_id = dungeonInstance.getInstanceId();
		this.camp_id = dataSection.getInt("id");
		this.spawnpoints = new ArrayList<Vector>();
		ConfigurationSection spawnpointsSection = dataSection.getConfigurationSection("spawnpoints");
		if(spawnpointsSection!=null){
			for(String key : spawnpointsSection.getKeys(false)){
				spawnpoints.add(spawnpointsSection.getVector(key));
			}
		}
		mobs = new HashMap<EntityType,Integer>();
		ConfigurationSection mobsSection = dataSection.getConfigurationSection("mobs");
		if(mobsSection!=null){
			for(String key : mobsSection.getKeys(false)){
				mobs.put(EntityType.valueOf(key.toUpperCase()), mobsSection.getInt(key));
			}
		}
		dungeonInstance.camps.put(camp_id, this);
	}
	public void trigger(Player player){
		prepareAll();
		enableSpawning(player);
	}
	public void enableSpawning(Player player){
		CampTriggerEvent event = new CampTriggerEvent(this.dungeon_id, this.instance_id, this.camp_id, player);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) return;
		AdventureDungeons.info("Enabled Spawning in camp "+camp_id+"!");
		this.spawning = true;
		if(isActive()){
			spawnAllPrepared();
		}
		switch(this.deathHandling){
		case "ANNIHILATE":
			if(this.getMobCount()>0){
				return;
			}
			this.attemptSpawning();
			break;
		case "REGENERATE":
			if(this.remaining_cooldown>0){
				return;
			}
			this.attemptSpawning();
			break;
		case "TRIGGER":
			
		default:
			return;
		}
	}
	public void disableSpawning(){
		AdventureDungeons.info("Disabled Spawning in camp "+camp_id+"!");
		spawning = false;
		if(this.cleanupTask!=null){
			this.cleanupTask.cancel();
		}
	}
	public void despawnMobs(){
		for(CampSpawnpoint spawnpoint : spawnpoints.values()){
			for(CampMob mob : spawnpoint.mobs){
				for(Entity entity : mob.live_entities){
					entity.remove();
					mob.prepared_count+=1;
				}
				mob.live_entities.clear();
			}
		}
	}
	public boolean isSpawning(){
		return spawning;
	}
	public void activate(){
		AdventureDungeons.info("Activated Spawning in camp "+camp_id+"!");
		this.active = true;
		this.spawning = true;
		if(this.respawnTask==null){
			Runnable respawn = new CampRespawnTask(this, "INSTANT", 0);
			this.respawnTask = Bukkit.getScheduler().runTaskLater(AdventureDungeons.plugin, respawn, 0);
		}
	}
	public void deactivate(){
		AdventureDungeons.info("Deactivated Spawning in camp "+camp_id+"!");
		this.active = false;
		this.spawning = false;
		for(CampSpawnpoint spawnpoint : spawnpoints.values()){
			spawnpoint.clear();
		}
		if(this.respawnTask!=null){
			respawnTask.cancel();
			this.respawnTask = null;
		}
		if(this.cleanupTask!=null){
			cleanupTask.cancel();
			this.cleanupTask = null;
		}
	}
	public boolean isActive(){
		return active;
	}
	public void attemptSpawning(){
		if(this.respawnTask!=null){
			return;
		}
		int waittime = this.respawn_timer;
			AdventureDungeons.info("Starte Regenerierungsprozess in "+waittime+" Sekunden!");
		 Runnable respawn = new CampRespawnTask(this, this.respawnHandling, waittime);
		 this.respawnTask = Bukkit.getScheduler().runTaskLater(AdventureDungeons.plugin, respawn, waittime*20L);
	}
	public int getMobCount(){
		int result = 0;
		for(CampSpawnpoint spawnpoint : spawnpoints.values()){
			for(CampMob mob : spawnpoint.mobs){
				result+=mob.live_entities.size();
			}
		}
		return result;
	}
	public static void loadCamps(DungeonInstance dungeonInstance) throws Exception{
		if(dungeonInstance==null) return;
		AdventureDungeons.info("Starting to load camps for world "+dungeonInstance.getWorld().getName());
		if(dungeonInstance.camps!=null){
			for(Camp oldCamp : dungeonInstance.camps.values()){
				oldCamp.deactivate();
				if(oldCamp.respawnTask!=null){
					oldCamp.respawnTask.cancel();
				}
				if(oldCamp.cleanupTask!=null){
					oldCamp.cleanupTask.cancel();
				}
			}
		}
		dungeonInstance.camps = new HashMap<Integer, Camp>();
		CampSpawnpoint.spawnpoints = new HashMap<Integer, CampSpawnpoint>();

		YamlConfiguration mmoCampsConfiguration = DataSource.getYamlResponse("adventure/camps.php", new String[]{
				"dungeon="+dungeonInstance.dungeon_id
		});
		for(String campIDstring : mmoCampsConfiguration.getKeys(false)){
			ConfigurationSection dataSection = mmoCampsConfiguration.getConfigurationSection(campIDstring);
			new Camp(dungeonInstance, dataSection);
		}
		AdventureDungeons.info("Finished loading camps for world "+dungeonInstance.getWorld().getName());
	}
	private void prepareAll(){
		for(CampSpawnpoint spawnpoint : spawnpoints.values()){
			for(CampMob mob : spawnpoint.mobs){
				mob.prepared_count = mob.max_count-mob.live_entities.size();
			}
		}
	}
	public int spawnAllPrepared(){
		int count = 0;
		for(CampSpawnpoint spawnpoint : spawnpoints.values()){
			for(CampMob mob : spawnpoint.mobs){
				count += mob.spawn();
			}
		}
		AdventureDungeons.info("Das Camp mit der ID hat "+count+" Mobs erschaffen.");
		return count;
	}
}
