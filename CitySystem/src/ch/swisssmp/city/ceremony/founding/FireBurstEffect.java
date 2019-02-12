package ch.swisssmp.city.ceremony.founding;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.utils.Random;
import ch.swisssmp.utils.Targetable;

public class FireBurstEffect {
	
	private final Location from;
	private final float size;
	private final Color colorA;
	private final Color colorB;
	Random random = new Random();
	
	private final List<LightParticles> particles = new ArrayList<LightParticles>();
	
	private FireBurstEffect(Location location, float size, Color colorA, Color colorB){
		this.from = location;
		this.size = size;
		this.colorA = colorA;
		this.colorB = colorB;
	}
	
	private void start(){
		this.spawnFlyingSparks();
		this.spawnFireSparks();
	}
	
	private void spawnFlyingSparks(){
		for(int i = 0; i < 15; i++){
			this.particles.add(spawnFlyingParticles(i*1));
		}
	}
	
	private void spawnFireSparks(){
		for(int i = 0; i < 15; i++){
			spawnFireParticles(i);
		}
	}
	
	private void spawnFireParticles(long delay){
		Location location = from.clone();
		Vector offset = random.insideUnitSphere();
		offset.multiply(random.nextDouble()*5);
		location.add(offset);
		Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), ()->{
			from.getWorld().spawnParticle(Particle.FLAME, from, 0, 0, 0.01, 0);
		}, delay);
	}
	
	private LightParticles spawnFlyingParticles(long delay){
		Vector vector = random.insideUnitSphere();
		double x = from.getX()+vector.getX() * size;
		double y = from.getY()+Math.abs(vector.getY()) * size;
		double z = from.getZ()+vector.getZ() * size;
		Vector delta = new Vector(from.getX()-x,from.getY()-y,from.getZ()-z);
		float yaw = (float)(Math.atan2(-1,0) - Math.atan2(delta.getZ(),delta.getX()));
		float pitch = (random.nextFloat()*2-1)*20;
		Location to = new Location(from.getWorld(),x,y,z,yaw,pitch);
		return LightParticles.spawn(from, new Targetable(to), delay, random.nextDouble()>0.3 ? colorA : colorB);
	}
	
	public void addOnFinishListener(Runnable runnable){
		if(runnable!=null) particles.get(particles.size()-1).addOnFinishListener(runnable);
	}
	
	public static FireBurstEffect play(Block block, float size, Color colorA, Color colorB){
		String sound = size>3 ? "fire_burst_big" : "fire_burst_small";
		Location location = new Location(block.getWorld(),block.getX()+0.5,block.getY()+0.5,block.getZ()+0.5,0,-90);
		FireBurstEffect result = new FireBurstEffect(location,size,colorA,colorB);
		result.start();
		block.getWorld().playSound(location, sound, SoundCategory.BLOCKS, 1f, 15f);
		return result;
	}
}
