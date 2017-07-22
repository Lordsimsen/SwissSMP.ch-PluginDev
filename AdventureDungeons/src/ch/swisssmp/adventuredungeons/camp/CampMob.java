package ch.swisssmp.adventuredungeons.camp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import ch.swisssmp.adventuredungeons.entity.MmoEntitySaveData;
import ch.swisssmp.adventuredungeons.world.AdventureWorld;
import ch.swisssmp.adventuredungeons.world.AdventureWorldInstance;

public class CampMob {
	public final int mmo_camp_mob_id;
	public static HashMap<UUID, CampMob> instances = new HashMap<UUID, CampMob>();
	public ArrayList<Entity> live_entities = new ArrayList<Entity>();
	public final CampSpawnpoint spawnpoint;
	public final EntityType entityType;
	/**
	 * @param There can only be as many mobs as count.
	 **/
	public final Integer max_count;
	/**
	 * @param This value represents the amount of mobs not currently in the world, but stored in memory until a player returns to this area.
	 **/
	public Integer prepared_count = 0;
	
	public CampMob(CampSpawnpoint spawnpoint, Integer mmo_camp_mob_id, EntityType entityType, Integer max_count){
		this.mmo_camp_mob_id = mmo_camp_mob_id;
		this.spawnpoint = spawnpoint;
		this.entityType = entityType;
		this.max_count = max_count;
		World world = spawnpoint.camp.world;
		AdventureWorldInstance mmoWorldInstance = AdventureWorld.getInstance(world);
		MmoEntitySaveData entitySaveData = mmoWorldInstance.entitySaveData;
		YamlConfiguration entityData = entitySaveData.entityData;
		for(Entity entity : world.getLivingEntities()){
			ConfigurationSection configurationSection = entityData.getConfigurationSection(entity.getUniqueId().toString());
			if(configurationSection==null) continue;
			int entity_camp_spawnpoint_id = configurationSection.getInt("mmo_camp_spawnpoint_id");
			int entity_camp_mob_id = configurationSection.getInt("mmo_camp_mob_id");
			if(entity_camp_spawnpoint_id==spawnpoint.mmo_camp_spawnpoint_id && entity_camp_mob_id==this.mmo_camp_mob_id){
				instances.put(entity.getUniqueId(), this);
				live_entities.add(entity);
			}
		}
		this.prepared_count = max_count-live_entities.size();
	}
	public int spawn(){
		int result = prepared_count;
		if(entityType==null){
			throw new NullPointerException("Keinen Mob gefunden!");
		}
		World world = spawnpoint.camp.world;
		AdventureWorldInstance mmoWorldInstance = AdventureWorld.getInstance(world);
		YamlConfiguration yamlConfiguration = mmoWorldInstance.entitySaveData.entityData;
		for(int i = 0; i < prepared_count; i++){
			Location baseLocation = spawnpoint.getSpawnLocation();
			if(baseLocation==null){
				throw new NullPointerException("Keinen Spawnpunkt gefunden!");
			}
			Entity entity = baseLocation.getWorld().spawnEntity(baseLocation, entityType);
			if(entity==null)
				continue;
			if(entity instanceof LivingEntity){
				LivingEntity livingEntity = (LivingEntity) entity;
				livingEntity.setRemoveWhenFarAway(false);
			}
			live_entities.add(entity);
			instances.put(entity.getUniqueId(), this);
			ConfigurationSection entitySection = yamlConfiguration.createSection(entity.getUniqueId().toString());
			entitySection.set("mmo_camp_spawnpoint_id", spawnpoint.mmo_camp_spawnpoint_id);
			entitySection.set("mmo_camp_mob_id", this.mmo_camp_mob_id);
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
