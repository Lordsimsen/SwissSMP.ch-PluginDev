package ch.swisssmp.adventuredungeons.mmocamp;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.adventuredungeons.Main;
import ch.swisssmp.adventuredungeons.mmoevent.MmoEvent;
import ch.swisssmp.adventuredungeons.mmoevent.MmoEventType;
import ch.swisssmp.adventuredungeons.mmoworld.MmoRegion;
import ch.swisssmp.adventuredungeons.mmoworld.MmoWorldInstance;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class MmoCamp {
	public final World world;
	public final int mmo_region_id;
	public final Integer mmo_camp_id;
	public final String deathHandling;
	public final String respawnHandling;
	public final Integer respawn_timer;
	public final HashMap<Integer, MmoCampSpawnpoint> spawnpoints;
	/**
	 * @param can be used to force enable/disable the camp.
	 */
	private boolean active = true;
	/**
	 * @param Is used by the system to determine whether it should spawn any mobs.
	 */
	private boolean spawning = false;
	public BukkitTask respawnTask = null;
	protected int remaining_cooldown = 0;
	public BukkitTask cleanupTask = null;

	public final boolean despawn_mobs;
	public final HashMap<MmoEventType, MmoEvent> events = new HashMap<MmoEventType, MmoEvent>();
	
	public MmoCamp(MmoWorldInstance worldInstance, ConfigurationSection dataSection){
		this.world = worldInstance.world;
		this.mmo_camp_id = dataSection.getInt("id");
		this.deathHandling = dataSection.getString("death_handling");
		this.respawnHandling = dataSection.getString("respawn_handling");
		this.respawn_timer = dataSection.getInt("respawn_timer");
		this.mmo_region_id = dataSection.getInt("region");
		if(mmo_region_id>0){
			MmoRegion mmoRegion = worldInstance.getRegion(this.mmo_region_id);
			if(mmoRegion==null){
				Main.debug("Region with mmo_region_id "+this.mmo_region_id+" could not be found.");
			}
			else if(!mmoRegion.registeredCamps.contains(this.mmo_camp_id)){
				mmoRegion.registeredCamps.add(this.mmo_camp_id);
			}
		}
		this.spawnpoints = new HashMap<Integer, MmoCampSpawnpoint>();
		ConfigurationSection spawnpointsSection = dataSection.getConfigurationSection("spawnpoints");
		if(spawnpointsSection!=null){
			for(String spawnpoint_key : spawnpointsSection.getKeys(false)){
				Integer spawnpoint_id = Integer.parseInt(spawnpoint_key);
				spawnpoints.put(spawnpoint_id, new MmoCampSpawnpoint(this, spawnpointsSection.getConfigurationSection(spawnpoint_key)));
			}
		}
		this.despawn_mobs = worldInstance.type.camps_despawn_mobs();
		MmoEvent.registerAll(dataSection, events);
		worldInstance.camps.put(mmo_camp_id, this);
	}
	protected MmoCampMob getIncomplete(){
		for(MmoCampSpawnpoint spawnpoint : spawnpoints.values()){
			MmoCampMob campMob = spawnpoint.getIncomplete();
			if(campMob!=null)
				return campMob;
		}
		return null;
	}
	protected boolean isRespawning(){
		return (this.respawnTask!=null);
	}
	public boolean canRespawn(){
		return (getIncomplete()!=null);
	}
	public void trigger(Player player){
		prepareAll();
		enableSpawning(player);
	}
	public void enableSpawning(Player player){
		MmoEvent.fire(events, MmoEventType.CAMP_TRIGGERED, player.getUniqueId());
		Main.info("Enabled Spawning in camp "+mmo_camp_id+"!");
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
		Main.info("Disabled Spawning in camp "+mmo_camp_id+"!");
		spawning = false;
		if(this.cleanupTask!=null){
			this.cleanupTask.cancel();
		}
		if(this.despawn_mobs){
			Runnable cleanup = new MmoCampCleanupTask(this);
			cleanupTask = Bukkit.getScheduler().runTaskLater(Main.plugin, cleanup, 600L);
		}
	}
	public void despawnMobs(){
		for(MmoCampSpawnpoint spawnpoint : spawnpoints.values()){
			for(MmoCampMob mob : spawnpoint.mobs){
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
		Main.info("Activated Spawning in camp "+mmo_camp_id+"!");
		this.active = true;
		this.spawning = true;
		if(this.respawnTask==null){
			Runnable respawn = new MmoCampRespawnTask(this, "INSTANT", 0);
			this.respawnTask = Bukkit.getScheduler().runTaskLater(Main.plugin, respawn, 0);
		}
	}
	public void deactivate(){
		Main.info("Deactivated Spawning in camp "+mmo_camp_id+"!");
		this.active = false;
		this.spawning = false;
		for(MmoCampSpawnpoint spawnpoint : spawnpoints.values()){
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
			Main.info("Starte Regenerierungsprozess in "+waittime+" Sekunden!");
		 Runnable respawn = new MmoCampRespawnTask(this, this.respawnHandling, waittime);
		 this.respawnTask = Bukkit.getScheduler().runTaskLater(Main.plugin, respawn, waittime*20L);
	}
	public int getMobCount(){
		int result = 0;
		for(MmoCampSpawnpoint spawnpoint : spawnpoints.values()){
			for(MmoCampMob mob : spawnpoint.mobs){
				result+=mob.live_entities.size();
			}
		}
		return result;
	}
	public synchronized static void loadCamps(MmoWorldInstance worldInstance) throws Exception{
		if(worldInstance==null) return;
		Main.info("Starting to load camps for world "+worldInstance.world.getName());
		if(worldInstance.camps!=null){
			for(MmoCamp oldCamp : worldInstance.camps.values()){
				oldCamp.deactivate();
				if(oldCamp.respawnTask!=null){
					oldCamp.respawnTask.cancel();
				}
				if(oldCamp.cleanupTask!=null){
					oldCamp.cleanupTask.cancel();
				}
			}
		}
		worldInstance.camps = new HashMap<Integer, MmoCamp>();
		MmoCampSpawnpoint.spawnpoints = new HashMap<Integer, MmoCampSpawnpoint>();
		for(MmoRegion mmoRegion : worldInstance.regions.values()){
			mmoRegion.registeredCamps.clear();
		}

		YamlConfiguration mmoCampsConfiguration = DataSource.getYamlResponse("camps.php", new String[]{
				"world="+worldInstance.system_name,
				"world_instance="+worldInstance.world.getName(),
		});
		for(String campIDstring : mmoCampsConfiguration.getKeys(false)){
			ConfigurationSection dataSection = mmoCampsConfiguration.getConfigurationSection(campIDstring);
			new MmoCamp(worldInstance, dataSection);
		}
		Main.info("Finished loading camps for world "+worldInstance.world.getName());
	}
	private void prepareAll(){
		for(MmoCampSpawnpoint spawnpoint : spawnpoints.values()){
			for(MmoCampMob mob : spawnpoint.mobs){
				mob.prepared_count = mob.max_count-mob.live_entities.size();
			}
		}
	}
	public int spawnAllPrepared(){
		int count = 0;
		for(MmoCampSpawnpoint spawnpoint : spawnpoints.values()){
			for(MmoCampMob mob : spawnpoint.mobs){
				count += mob.spawn();
			}
		}
		Main.info("Das Camp mit der ID hat "+count+" Mobs erschaffen.");
		return count;
	}
}
