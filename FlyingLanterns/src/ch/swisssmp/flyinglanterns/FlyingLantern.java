package ch.swisssmp.flyinglanterns;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
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
		this.rotation = (random.nextFloat()*2-1)*0.3f;
		this.motion = new Vector((random.nextDouble()*2-1)*0.3,1,(random.nextDouble()*2-1)*0.3);
		lanternMap.put(entity, this);
	}
	
	public Location getLocation(){
		return this.entity.getLocation().add(0,0.75,0);
	}
	
	public void drop(){
		ItemStack itemStack = FlyingLanterns.getFlyingLantern();
		this.entity.getWorld().dropItem(this.getLocation(), itemStack);
		this.entity.getWorld().playSound(this.getLocation(), Sound.ENTITY_ITEMFRAME_REMOVE_ITEM, 10, 1);
		this.remove();
	}

	@Override
	public void run() {
		this.speed = Mathf.clamp(this.speed+0.0001, 0, this.maxSpeed);
		this.nextLocation = entity.getLocation().add(this.motion.clone().multiply(this.speed));
		if(this.nextLocation.clone().add(0, 1, 0).getBlock().getType().isSolid()){
			this.drop();
			return;
		}
		this.nextLocation.setYaw(this.nextLocation.getYaw()+this.rotation);
		this.entity.teleport(this.nextLocation);
		this.flameCountdown--;
		if(this.flameCountdown<0){
			entity.getWorld().spawnParticle(Particle.FLAME, entity.getLocation().add(0,0.5,0), 1, 0.02,0.02,0.02, 0.003, null);
			this.flameCountdown = 4;
		}
		if(entity.getLocation().getY()>300) this.remove();
	}
	
	protected void unload(){
		this.task.cancel();
		FlyingLantern.lanternMap.remove(this.entity);
	}
	
	protected void explode(){
		Location location = this.getLocation();
		Firework firework = (Firework)location.getWorld().spawnEntity(location, EntityType.FIREWORK);
		FireworkMeta fireworkMeta = firework.getFireworkMeta();
		
        //Get the type
        Type type = Type.BURST;
       
        //Create our effect with this
        FireworkEffect effect = FireworkEffect.builder()
        		.flicker(true)
        		.withColor(Color.RED, Color.WHITE)
        		.withFade(Color.YELLOW)
        		.with(type)
        		.trail(random.nextBoolean())
        		.build();
       
        //Then apply the effect to the meta
        fireworkMeta.addEffect(effect);
       
        //Generate some random power and set it
        int rp = random.nextInt(2) + 1;
        fireworkMeta.setPower(rp);
       
        //Then apply this to our rocket
        firework.setFireworkMeta(fireworkMeta);
        
        Bukkit.getScheduler().runTaskLater(FlyingLanterns.plugin, new Runnable(){
        	public void run(){
        		firework.detonate();
        	}
        }, 1L);
		this.remove();
	}
	
	protected void remove(){
		this.unload();
		this.entity.remove();
	}
	
	protected static FlyingLantern spawn(Location location){
		location = location.clone();
		location.setYaw(random.nextFloat()*360f);
		ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		armorStand.getEquipment().setHelmet(FlyingLanterns.getFlyingLantern());
		armorStand.setVisible(false);
		armorStand.setGravity(false);
		armorStand.setInvulnerable(true);
		armorStand.setSmall(true);
		location.getWorld().playSound(location, Sound.BLOCK_CLOTH_PLACE, 5, 1);
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
