package ch.swisssmp.flyinglanterns;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.Random;

public class FlyingLantern implements Runnable{
	private static HashMap<Entity,FlyingLantern> lanternMap = new HashMap<Entity,FlyingLantern>();
	private static Random random = new Random();
	
	private final Entity entity;
	private final double maxSpeed;
	private final float rotation;
	private Vector motion;
	private double speed = 0.002;
	private int flameCountdown = 0;
	
	private Location nextLocation;
	
	private BukkitTask task;
	
	private FlyingLantern(Entity entity){
		this.entity = entity;
		this.maxSpeed = Mathf.clamp(random.nextDouble()*0.05, 0.01, 0.05);
		this.rotation = (random.nextFloat()*2-1)*0.01f;
		this.motion = new Vector((random.nextDouble()*2-1)*0.3,1,(random.nextDouble()*2-1)*0.3);
		lanternMap.put(entity, this);
	}

	@Override
	public void run() {
		this.speed = Mathf.clamp(this.speed+0.0001, 0, this.maxSpeed);
		this.nextLocation = entity.getLocation().add(this.motion.clone().multiply(this.speed));
		this.nextLocation.setYaw(this.nextLocation.getYaw()+this.rotation);
		this.entity.teleport(this.nextLocation);
		this.flameCountdown--;
		if(this.flameCountdown<0){
			entity.getWorld().spawnParticle(Particle.FLAME, entity.getLocation().add(0,1.5,0), 1, 0.02,0.02,0.02, 0.003, null);
			this.flameCountdown = 4;
		}
		if(entity.getLocation().getY()>300) this.remove();
	}
	
	protected void unload(){
		this.task.cancel();
		FlyingLantern.lanternMap.remove(this.entity);
	}
	
	private void remove(){
		this.unload();
		this.entity.remove();
	}
	
	protected static FlyingLantern spawn(Location location){
		location = location.clone();
		location.setYaw(random.nextFloat()*360f);
		ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location.subtract(0, 1.5, 0), EntityType.ARMOR_STAND);
		armorStand.getEquipment().setHelmet(FlyingLanterns.getFlyingLantern());
		armorStand.setVisible(false);
		armorStand.setGravity(false);
		armorStand.setInvulnerable(true);
		return FlyingLantern.load(armorStand);
	}
	
	protected static FlyingLantern load(Entity entity){
		FlyingLantern lantern = new FlyingLantern(entity);
		lantern.task = Bukkit.getScheduler().runTaskTimer(FlyingLanterns.plugin, lantern, 0, 1);
		return lantern;
	}
	
	protected static FlyingLantern get(Entity entity){
		return lanternMap.get(entity);
	}
}
