package ch.swisssmp.lift;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import ch.swisssmp.lift.effect.LiftTravelEffect;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import ch.swisssmp.lift.event.LiftEnterEvent;
import ch.swisssmp.lift.event.LiftExitEvent;
import ch.swisssmp.utils.SwissSMPler;

public class LiftTravel implements Runnable, Listener {
	private final static HashMap<LiftInstance,LiftTravel> travels = new HashMap<LiftInstance,LiftTravel>();

	private final World world;
	private final LiftInstance lift;
	private final LiftFloor from;
	private final LiftFloor to;
	
	private final double fromY;
	private final double toY;
	
	private long t;
	private double height;
	private double direction;
	private long duration;
	private Vector speed;
	private LiftTravelEffect effect;
	
	private BukkitTask task;
	
	private final List<Entity> entities = new ArrayList<Entity>();
	private HashMap<Block,BlockData> originalData;
	
	private LiftTravel(LiftFloor from, LiftFloor to){
		this.world = from.getLift().getWorld();
		this.lift = from.getLift();
		this.from = from;
		this.to = to;
		this.fromY = from.getY()+1;
		this.toY = to.getY()+1.5;
	}
	
	private void initialize(){
		height = Math.abs(fromY - toY);
		LiftType type = lift.getType();
		long timeInSeconds = Math.max(1, Math.round(height / type.getSpeed()));
		t = 0;
		direction = fromY <= toY ? 1 : -1;
		duration = timeInSeconds*20;
		speed = new Vector(0, type.getSpeed() * direction / 20, 0);
		effect = type.getTravelEffect();
		originalData = this.clearShaft(this.lift);
		task = Bukkit.getScheduler().runTaskTimer(LiftPlugin.getInstance(), this, 0, 1);
		Bukkit.getPluginManager().registerEvents(this, LiftPlugin.getInstance());
		travels.put(from.getLift(), this);
	}
	
	@Override
	public void run() {
		t++;
		double progress = (double) t / duration;
		double y = fromY + height * progress * direction;
		this.updatePassengers(y);
		for(Entity entity : entities){
			if(entity.getVehicle()!=null) continue;
			Location location = entity.getLocation();
			double delta = y-location.getY();
			if(Math.abs(delta)>4){
				location.setY(y);
				teleportEntityWithPassengers(entity,location);
			}
			Vector velocity = new Vector(0,speed.getY()+delta/20,0);
			entity.setVelocity(velocity);
			entity.setFallDistance(0);
			if(effect!=null) effect.play(entity,velocity, t);
		}
		if(t>=duration){
			complete();
		}
	}
	
	@EventHandler
	private void onEntityDeath(EntityDeathEvent event){
		if(!this.entities.contains(event.getEntity())) return;
		removePassenger(event.getEntity());
	}
	
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event){
		if(!this.entities.contains(event.getPlayer())) return;
		removePassenger(event.getPlayer());
	}
	
	@EventHandler
	private void onPlayerToggleFlight(PlayerToggleFlightEvent event){
		if(entities.contains(event.getPlayer())) event.setCancelled(true);
	}
	
	private void teleportEntityWithPassengers(Entity entity, Location location){
		List<Entity> passengers = entity.getPassengers();
		for(Entity passenger : passengers){
			entity.removePassenger(passenger);
		}
		entity.teleport(location);
		for(Entity passenger : passengers){
			entity.addPassenger(passenger);
		}
	}
	
	private void complete(){
		if(direction>0){
			List<Entity> entities = new ArrayList<Entity>(this.entities);
			teleportEntitiesToDestination(entities);
			Bukkit.getScheduler().runTaskLater(LiftPlugin.getInstance(), ()->{
				teleportEntitiesToDestination(entities);
			}, 1l);
		}
		for(Entity entity : entities){
			if(!(entity instanceof Player)) continue;
			Player player = (Player) entity;
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 4, 1.5f);
			SwissSMPler.get(player).sendActionBar(to.getName());
		}
		finish();
	}
	
	private void teleportEntitiesToDestination(List<Entity> entities){
		double targetY = toY;
		for(Entity entity : entities){
			if(entity.getVehicle()!=null) continue;
			entity.setVelocity(new Vector());
			Location location = entity.getLocation();
			double safetyGap = entity instanceof Player ? 0 : (entity.getPassengers().size()>0 ? 1 : 0.5);
			if(location.getY()<targetY+safetyGap){
				location.setY(targetY+safetyGap);
				teleportEntityWithPassengers(entity,location);
			}
		}
	}
	
	public void cancel(){
		finish();
	}
	
	private void finish(){
		for(Entity entity : new ArrayList<Entity>(entities)){
			this.removePassenger(entity);
		}
		this.resetShaft(originalData);
		if(task!=null) task.cancel();
		travels.remove(lift);
		HandlerList.unregisterAll(this);
	}
	
	private HashMap<Block,BlockData> clearShaft(LiftInstance instance){
		HashMap<Block,BlockData> result = new HashMap<Block,BlockData>();
		int lowerY = Math.min(from.getY(), to.getY());
		int upperY = Math.max(from.getY(), to.getY());
		for(LiftFloor floor : instance.getIntermediateFloors()){
			if(floor.getY()<=lowerY || floor.getY() > upperY) continue;
			for(Block block : floor.getBlocks()){
				BlockData data = block.getBlockData().clone();
				result.put(block, data);
				block.setType(Material.AIR);
			}
		}
		return result;
	}
	
	private void resetShaft(HashMap<Block,BlockData> original){
		for(Entry<Block,BlockData> entry : original.entrySet()){
			Block block = entry.getKey();
			BlockData data = entry.getValue();
			block.setBlockData(data);
		}
	}
	
	private void updatePassengers(double y){
		Collection<Entity> entities = this.getEntities(world, lift);
		for(Entity entity : entities){
			if(this.entities.contains(entity) || !entity.hasGravity()) continue;
			if(Math.abs(entity.getLocation().getY() - y)>5) continue;
			addPassenger(entity);
		}
		Collection<Entity> leftEntities = new ArrayList<Entity>(3);
		for(Entity entity : this.entities){
			if(entities.contains(entity)) continue;
			leftEntities.add(entity);
		}
		for(Entity entity : leftEntities){
			removePassenger(entity);
		}
		if(this.entities.size()==0) this.cancel();
	}
	
	private boolean addPassenger(Entity entity){
		LiftEnterEvent event = new LiftEnterEvent(entity, this);
		try{
			Bukkit.getPluginManager().callEvent(event);
		}
		catch(Exception e){
			Debug.Log(e);
		}
		if(event.isCancelled()) return false;
		if(entity instanceof Player){
			Player player = (Player) entity;
			player.setAllowFlight(true);
			player.setFlying(true);
			player.setAllowFlight(false);
		}
		entity.setGravity(false);
		this.entities.add(entity);
		return true;
	}
	
	private void removePassenger(Entity entity){
		this.entities.remove(entity);
		if(entity instanceof Player){
			Player player = (Player) entity;
			player.setFlying(false);
			player.setAllowFlight(player.getGameMode()!=GameMode.ADVENTURE && player.getGameMode()!=GameMode.SURVIVAL);
		}
		entity.setGravity(true);
		entity.setVelocity(new Vector());
		LiftExitEvent event = new LiftExitEvent(entity, this);
		try{
			Bukkit.getPluginManager().callEvent(event);
		}
		catch(Exception e){
			Debug.Log(e);
		}
	}
	
	private Collection<Entity> getEntities(World world, LiftInstance lift){
		return world.getNearbyEntities(lift.getBoundingBox(), e->e instanceof LivingEntity);
	}

	protected static void cancelAll(){
		for(LiftTravel travel : travels.values()){
			travel.cancel();
		}
	}
	
	public static LiftTravel get(LiftInstance instance){
		return travels.get(instance);
	}
	
	public static LiftTravel start(LiftFloor from, LiftFloor to){
		if(travels.containsKey(from.getLift())) return null;
		LiftTravel result = new LiftTravel(from, to);
		result.initialize();
		return result;
	}
}
