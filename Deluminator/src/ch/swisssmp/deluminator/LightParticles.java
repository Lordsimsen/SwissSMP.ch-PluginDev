package ch.swisssmp.deluminator;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.utils.Mathf;

public class LightParticles implements Runnable {

	private final World world;
	private final Location from;
	private final Targetable to;
	private final Color color;
	private final float step;
	private final int density;

	private Location targetLocation;
	private float t = 0;
	private Location location;
	
	private BukkitTask task;
	
	private LightParticles(Location from, Targetable to, long time, int density){
		this.world = from.getWorld();
		this.from = from;
		this.to = to;
		this.color = Color.fromRGB(255, 230, 80);
		this.step = 1f/(time*density);
		this.density = density;
	}
	
	@Override
	public void run() {
		if(t>=1){
			this.task.cancel();
			return;
		}
		for(int i = 0; i < this.density; i++){
			this.spawnParticle(t);
			this.t+=this.step;
		}
	}
	
	private void spawnParticle(float t){
		this.targetLocation = this.to.getLocation();
		this.location = new Location(this.world,Mathf.lerp(from.getX(), targetLocation.getX(), t), Mathf.lerp(from.getY(), targetLocation.getY(), t), Mathf.lerp(from.getZ(), targetLocation.getZ(), t));
		this.world.spawnParticle(Particle.REDSTONE, this.location.getX(),this.location.getY(),this.location.getZ(),0,Math.max(this.color.getRed()/255f, Float.MIN_VALUE),this.color.getGreen()/255f,this.color.getBlue()/255f,1);
	}
	
	protected static LightParticles spawn(Location from, Targetable to){
		LightParticles result = new LightParticles(from,to, 6, 4);
		result.task = Bukkit.getScheduler().runTaskTimer(DeluminatorPlugin.plugin, result, 0, 1);
		return result;
	}
}
