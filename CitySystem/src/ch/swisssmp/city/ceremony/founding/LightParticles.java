package ch.swisssmp.city.ceremony.founding;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.utils.Random;
import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.Targetable;
import ch.swisssmp.utils.splines.Spline3D;

public class LightParticles implements Runnable {

	private final World world;
	private final Location from;
	private final Vector fromHandle;
	private final Targetable to;
	private final Color color;
	private final float step;
	private final int density;
	private final Random random = new Random();

	private Location targetLocation;
	private double arkSize;
	private Vector targetHandle;
	private float t = 0;
	private Spline3D spline;
	
	private BukkitTask task;
	
	private List<Runnable> onFinishListeners = new ArrayList<Runnable>();
	
	private LightParticles(Location from, Targetable to, long time, int density, Color color){
		this.world = from.getWorld();
		this.from = from;
		this.fromHandle = from.toVector().add(from.getDirection().multiply(Mathf.clamp(to.getLocation().distanceSquared(from)*0.1, 0, 3)));
		this.to = to;
		this.color = color;
		this.step = 1f/(time*density);
		this.density = density;
	}
	
	@Override
	public void run() {
		if(t>=1){
			this.task.cancel();
			for(Runnable runnable : this.onFinishListeners){
				try{
					runnable.run();
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			return;
		}
		for(int i = 0; i < this.density; i++){
			try{
				this.spawnParticle(t);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			this.t+=this.step;
		}
	}
	
	protected void addOnFinishListener(Runnable runnable){
		this.onFinishListeners.add(runnable);
	}
	
	private void spawnParticle(float t){
		this.targetLocation = this.to.getLocation();
		this.arkSize = Mathf.clamp(this.targetLocation.distanceSquared(this.from)*0.1, 0, 3);
		this.targetHandle = this.targetLocation.toVector().add(this.targetLocation.getDirection().multiply(this.arkSize));
		this.spline = new Spline3D(this.from.toVector(), this.fromHandle, this.targetHandle, this.targetLocation.toVector());
		Vector vector = this.spline.getPositionAt(t*3).add(random.insideUnitSphere().multiply(0.1));
		Location location = new Location(world,vector.getX(),vector.getY(),vector.getZ());
		DustOptions dustOptions = new DustOptions(color,1);
		this.world.spawnParticle(Particle.REDSTONE, location, 1, dustOptions);
	}
	
	public static LightParticles spawn(Location from, Targetable to, long delay, Color color){
		LightParticles result = new LightParticles(from,to, Mathf.roundToInt(from.distance(to.getLocation())*3.5), 4, color);
		result.task = Bukkit.getScheduler().runTaskTimer(CitySystemPlugin.getInstance(), result, delay, 1);
		return result;
	}
}

