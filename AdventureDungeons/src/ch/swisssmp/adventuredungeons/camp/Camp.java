package ch.swisssmp.adventuredungeons.camp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import ch.swisssmp.adventuredungeons.event.CampClearEvent;
import ch.swisssmp.adventuredungeons.event.CampClearedEvent;
import ch.swisssmp.adventuredungeons.event.CampTriggerEvent;
import ch.swisssmp.adventuredungeons.world.DungeonInstance;
import ch.swisssmp.utils.ConfigurationSection;

public class Camp {
	private final DungeonInstance dungeonInstance;
	private final int camp_id;
	private final String name;
	private final List<Vector> spawnpoints;
	private final List<CampMob> mobs;
	private final double playerFactor;
	private final List<Entity> liveEntities = new ArrayList<Entity>();
	private boolean triggered = false;
	
	public Camp(DungeonInstance dungeonInstance, ConfigurationSection dataSection){
		this.dungeonInstance = dungeonInstance;
		this.camp_id = dataSection.getInt("id");
		this.name = dataSection.getString("name");
		this.spawnpoints = new ArrayList<Vector>();
		ConfigurationSection spawnpointsSection = dataSection.getConfigurationSection("spawnpoints");
		if(spawnpointsSection!=null){
			for(String key : spawnpointsSection.getKeys(false)){
				spawnpoints.add(spawnpointsSection.getVector(key));
			}
		}
		mobs = new ArrayList<CampMob>();
		ConfigurationSection mobsSection = dataSection.getConfigurationSection("mobs");
		if(mobsSection!=null){
			for(String key : mobsSection.getKeys(false)){
				CampMob campMob = CampMob.get(mobsSection.getConfigurationSection(key));
				if(campMob!=null){
					mobs.add(campMob);
				}
			}
		}
		this.playerFactor = dataSection.getDouble("player_factor");
	}
	public int getCampId(){
		return this.camp_id;
	}
	public int getMobCount(){
		return this.liveEntities.size();
	}
	public String getName(){
		return this.name;
	}
	public DungeonInstance getDungeonInstance(){
		return this.dungeonInstance;
	}
	public Entity[] getLiveEntities(){
		return this.liveEntities.toArray(new Entity[this.liveEntities.size()]);
	}
	public Entity getLiveEntity(){
		if(this.liveEntities.size()==0) return null;
		return this.liveEntities.get(0);
	}
	public void trigger(Player player, boolean force){
		if(this.triggered && !force) return;
		CampTriggerEvent event = new CampTriggerEvent(this, player);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) return;
		this.triggered = true;
		Integer index;
		Random random = new Random();
		Vector vector;
		Location location;
		Entity entity;
		LivingEntity livingEntity;
		for(CampMob campMob : this.mobs){
			for(int i = 0; i < (campMob.getAmount() * (1+this.playerFactor*this.getDungeonInstance().getPlayers().size())); i++){
				index = random.nextInt(this.spawnpoints.size());
				vector = this.spawnpoints.get(index);
				location = new Location(this.dungeonInstance.getWorld(), vector.getX()+0.5, vector.getY()+0.2, vector.getZ()+0.5);
				entity = this.dungeonInstance.spawnEntity(location, campMob.getEntityType(), this.camp_id);
				if(campMob.hasCustomName()){
					entity.setCustomName(campMob.getCustomName());
				}
				this.liveEntities.add(entity);
				if(entity instanceof LivingEntity){
					livingEntity = (LivingEntity)entity;
					livingEntity.setRemoveWhenFarAway(false);
				}
			}
		}
	}
	public void manageEntityDeath(Entity entity){
		this.liveEntities.remove(entity);
		this.dungeonInstance.removeEntity(entity);
		for(Entity remainingEntity : this.getLiveEntities()){
			if(!(remainingEntity instanceof LivingEntity)) continue;
			if(((LivingEntity)remainingEntity).getHealth()<=0){
				this.liveEntities.remove(remainingEntity);
				this.dungeonInstance.removeEntity(remainingEntity);
			}
		}
		if(this.liveEntities.size()==0){
			for(Player player : this.getDungeonInstance().getWorld().getPlayers()){
				Bukkit.getPluginManager().callEvent(new CampClearEvent(this, player));
			}
			Bukkit.getPluginManager().callEvent(new CampClearedEvent(this));
		}
	}
	/**
	 * Must only be used to force close the camp
	 */
	public void despawnMobs(){
		for(Entity entity : this.liveEntities){
			entity.remove();
		}
		this.liveEntities.clear();
		this.triggered = false;
	}
}
