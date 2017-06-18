package ch.swisssmp.craftmmo.mmocamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

import ch.swisssmp.craftmmo.mmoentity.IControllable;
import ch.swisssmp.craftmmo.mmoentity.MmoEntitySaveData;
import ch.swisssmp.craftmmo.mmoentity.MmoMob;

public class MmoCampMob {
	public static ArrayList<IControllable> pendingAssignments = new ArrayList<IControllable>();
	public final int mmo_camp_mob_id;
	public static HashMap<UUID, MmoCampMob> instances = new HashMap<UUID, MmoCampMob>();
	public ArrayList<Entity> live_entities = new ArrayList<Entity>();
	public final MmoCampSpawnpoint spawnpoint;
	public final int mmo_mob_id;
	/**
	 * @param There can only be as many mobs as count.
	 **/
	public final Integer max_count;
	/**
	 * @param This value represents the amount of mobs not currently in the world, but stored in memory until a player returns to this area.
	 **/
	public Integer prepared_count = 0;
	
	public MmoCampMob(MmoCampSpawnpoint spawnpoint, Integer mmo_camp_mob_id, int mmo_mob_id, Integer max_count){
		this.mmo_camp_mob_id = mmo_camp_mob_id;
		this.spawnpoint = spawnpoint;
		this.mmo_mob_id = mmo_mob_id;
		this.max_count = max_count;
		ArrayList<IControllable> usedAssignments = new ArrayList<IControllable>();
		for(IControllable controllable : pendingAssignments){
			MmoEntitySaveData data = controllable.getSaveData();
			if(data.mmo_camp_spawnpoint_id==spawnpoint.mmo_camp_spawnpoint_id && data.mmo_camp_mob_id==this.mmo_camp_mob_id){
				instances.put(controllable.getEntity().getUniqueID(), this);
				live_entities.add(controllable.getEntity().getBukkitEntity());
				usedAssignments.add(controllable);
			}
		}
		for(IControllable controllable : usedAssignments){
			pendingAssignments.remove(controllable);
		}
		this.prepared_count = max_count-usedAssignments.size();
	}
	public int spawn(){
		int result = prepared_count;
		MmoMob mmoMob = MmoMob.get(this.mmo_mob_id);
		if(mmoMob==null){
			throw new NullPointerException("Keinen Mob gefunden!");
		}
		for(int i = 0; i < prepared_count; i++){
			Location baseLocation = spawnpoint.getSpawnLocation();
			if(baseLocation==null){
				throw new NullPointerException("Keinen Spawnpunkt gefunden!");
			}
			Entity entity = mmoMob.spawnInstance(baseLocation.add(0.5, 0.5, 0.5));
			if(entity==null)
				continue;
			live_entities.add(entity);
			instances.put(entity.getUniqueId(), this);
			MmoEntitySaveData data = ((IControllable)((CraftEntity)entity).getHandle()).getSaveData();
			data.mmo_camp_spawnpoint_id = spawnpoint.mmo_camp_spawnpoint_id;
			data.mmo_camp_mob_id = this.mmo_camp_mob_id;
		}
		prepared_count = 0;
		return result;
	}
	public void despawn(){
		for(Entity entity : live_entities){
			entity.remove();
		}
		live_entities.clear();
	}
}
