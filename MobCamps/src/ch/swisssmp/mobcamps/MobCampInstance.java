package ch.swisssmp.mobcamps;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.scheduler.BukkitTask;

public class MobCampInstance implements Runnable{
	private static HashMap<Entity,MobCampInstance> instances = new HashMap<Entity,MobCampInstance>();
	
	private final Entity entity;
	private final MobCamp mobCamp;
	
	private MobCampInstanceState state;
	private BukkitTask task;
	
	private MobCampInstance(Entity entity, MobCamp mobCamp){
		this.entity = entity;
		this.mobCamp = mobCamp;
		instances.put(this.entity, this);
		this.updateState();
	}
	
	public Entity getEntity(){
		return this.entity;
	}
	
	public MobCamp getMobCamp(){
		return this.mobCamp;
	}
	
	public MobCampInstanceState getState(){
		return this.state;
	}

	@Override
	public void run() {
		if(!entity.isValid()){
			if(entity.getLocation().getChunk().isLoaded()){
				this.remove();
			}
			else{
				this.unload();
			}
			return;
		}
		if(!this.canSpawn()) return;
		this.mobCamp.spawnAt(this.entity.getLocation().add(0,1.5,0));
	}
	
	private boolean canSpawn(){
		Entity spawner = this.entity.getPassengers().get(0);
		if(spawner==null) return false;
		if(spawner.getLocation().getBlock().getLightLevel()>7) return false;
		return this.mobCamp.canSpawn(this.entity);
	}
	
	protected void unload(){
		instances.remove(this.entity);
		if(this.task!=null) this.task.cancel();
	}
	
	protected void remove(){
		this.unload();
		for(Entity passenger : new ArrayList<Entity>(this.entity.getPassengers())){
			passenger.remove();
		}
		this.entity.remove();
	}
	
	public void updateState(){
		if(this.entity.getWorld().getGameRuleValue("doMobCampSpawning").toLowerCase().equals("true")){
			if(this.task==null) this.task = Bukkit.getScheduler().runTaskTimer(MobCamps.plugin, this, 100, 200);
			for(Entity passenger : this.entity.getPassengers()) passenger.remove();
			this.state = MobCampInstanceState.ACTIVE;
		}
		else{
			this.state = MobCampInstanceState.EDITOR;
			this.updateDisplay();
		}
	}
	
	private void updateDisplay(){
		for(Entity passenger : this.entity.getPassengers()){
			passenger.remove();
		}
		EntityType displayType;
		Inventory contents = this.mobCamp.getContents();
		int firstEggIndex = contents.first(Material.MONSTER_EGG);
		if(firstEggIndex>=0) displayType = ((SpawnEggMeta)contents.getItem(firstEggIndex).getItemMeta()).getSpawnedType();
		else displayType = EntityType.ZOMBIE;
		Entity display = this.entity.getWorld().spawnEntity(this.entity.getLocation(), displayType);
		display.setCustomName("§a"+this.mobCamp.getName());
		display.setCustomNameVisible(false);
		display.setInvulnerable(true);
		display.setSilent(true);
		((LivingEntity)display).setRemoveWhenFarAway(false);
		this.entity.addPassenger(display);
	}
	
	public static MobCampInstance create(Location location, MobCamp mobCamp){
		Location armorStandLocation = location.clone();
		armorStandLocation.subtract(0,1.5,0);
		ArmorStand armorStand = (ArmorStand)location.getWorld().spawnEntity(armorStandLocation, EntityType.ARMOR_STAND);
		armorStand.setInvulnerable(true);
		armorStand.setGravity(false);
		armorStand.setVisible(false);
		armorStand.setCustomName("§rCamp_"+mobCamp.getCampId());
		armorStand.setCustomNameVisible(false);
		MobCampInstance result = new MobCampInstance(armorStand,mobCamp);
		mobCamp.updateInstance(result);
		return result;
	}
	
	public static MobCampInstance get(Entity entity){
		if(entity.isInsideVehicle()) return MobCampInstance.get(entity.getVehicle());
		if(instances.containsKey(entity)) return instances.get(entity);
		MobCamp mobCamp = MobCamp.get(entity);
		if(mobCamp==null){
			if(!entity.isInsideVehicle()) return null;
			else return MobCampInstance.get(entity.getVehicle());
		}
		return new MobCampInstance(entity,mobCamp);
	}
	
	protected static void loadAll(){
		for(World world : Bukkit.getWorlds()){
			MobCampInstance.loadAll(world);
		}
	}
	
	protected static void loadAll(World world){
		for(Chunk chunk : world.getLoadedChunks()){
			MobCampInstance.loadAll(chunk);
		}
	}
	
	protected static void loadAll(Chunk chunk){
		MobCampInstance instance;
		for(Entity entity : chunk.getEntities()){
			instance = MobCampInstance.get(entity);
			if(instance!=null) instance.updateState();
		}
	}
	
	protected static void unloadAll(Chunk chunk){
		MobCampInstance instance;
		for(Entity entity : chunk.getEntities()){
			instance = MobCampInstance.get(entity);
			if(instance==null) continue;
			instance.unload();
		}
	}
	
	protected static void updateAll(MobCamp mobCamp){
		for(MobCampInstance instance : instances.values()){
			if(instance.mobCamp != mobCamp) continue;
			mobCamp.updateInstance(instance);
		}
	}
}
