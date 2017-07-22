package ch.swisssmp.adventuredungeons.camp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.event.CampClearEvent;
import ch.swisssmp.utils.ConfigurationSection;

public class CampSpawnpoint {
	public static HashMap<Integer, CampSpawnpoint> spawnpoints;
	
	public final int mmo_camp_spawnpoint_id;
	public final Camp camp;
	public final ArrayList<Location> locations = new ArrayList<Location>();
	private final Random random;
	public ArrayList<CampMob> mobs = new ArrayList<CampMob>();
	
	public CampSpawnpoint(Camp camp, ConfigurationSection dataSection){
		this.mmo_camp_spawnpoint_id = Integer.parseInt(dataSection.getName());
		this.camp = camp;
		ConfigurationSection coordinatesSection = dataSection.getConfigurationSection("coordinates");
		World world = camp.world;
		for(String key : coordinatesSection.getKeys(false)){
			ConfigurationSection coordinateSection = coordinatesSection.getConfigurationSection(key);
			Integer x = coordinateSection.getInt("x");
			Integer y = coordinateSection.getInt("y");
			Integer z = coordinateSection.getInt("z");
			Location location = new Location(world, x, y, z);
			locations.add(location);
		}
		ConfigurationSection mobsSection = dataSection.getConfigurationSection("mobs");
		if(mobsSection!=null){
			for(String key : mobsSection.getKeys(false)){
				ConfigurationSection mobSection = mobsSection.getConfigurationSection(key);
				EntityType entityType;
				try{
					entityType = EntityType.valueOf(mobSection.getString("entity_type"));
				}
				catch(Exception e){
					e.printStackTrace();
					entityType = null;
				}
				if(entityType==null) continue;
				Integer mmo_camp_mob_id = Integer.parseInt(mobSection.getName());
				Integer count = mobSection.getInt("amount");
				this.mobs.add(new CampMob(this, mmo_camp_mob_id, entityType, count));
			}
		}
		this.random = new Random();
		spawnpoints.put(this.mmo_camp_spawnpoint_id, this);
	}
	public CampMob getIncomplete(){
		for(CampMob campMob : mobs){
			if(campMob.live_entities.size() < campMob.max_count - campMob.prepared_count){
				return campMob;
			}
		}
		return null;
	}
	public void manageEntityDeath(CampMob campMob, Entity entity, Player player){
		AdventureDungeons.info("Eine Einheit '"+entity.getCustomName()+"' ist gestorben.");
		campMob.live_entities.remove(entity);
		if(camp.getMobCount()==0){
			CampClearEvent event = new CampClearEvent(this.camp.dungeon_id, this.camp.instance_id, this.camp.camp_id);
			Bukkit.getPluginManager().callEvent(event);
		}
		if(!camp.isActive() || !camp.isSpawning())
			return;
		switch(camp.deathHandling){
		case "ANNIHILATE":
			if(camp.getMobCount()>0){
				return;
			}
			break;
		case "REGENERATE":
			break;
		default:
			return;
		}
		this.camp.attemptSpawning();
	}
	public void clear(){
		for(CampMob campMob : mobs){
			campMob.despawn();
		}
	}
	public Location getSpawnLocation(){
		int index = random.nextInt(locations.size());
		return locations.get(index);
	}
	public static CampSpawnpoint get(int mmo_camp_spawnpoint_id){
		if(spawnpoints==null){
			return null;
		}
		return spawnpoints.get(mmo_camp_spawnpoint_id);
	}
}
