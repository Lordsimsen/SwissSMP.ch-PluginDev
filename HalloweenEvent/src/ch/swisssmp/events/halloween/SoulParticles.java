package ch.swisssmp.events.halloween;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.Targetable;
import ch.swisssmp.utils.splines.Spline3D;

public class SoulParticles implements Runnable {

	private final World world;
	private final Location from;
	private final Vector fromHandle;
	private final Targetable to;
	private final Color color;
	private final float step;
	private final int density;

	private Location targetLocation;
	private Vector targetHandle;
	private float t = 0;
	private Spline3D spline;
	private Vector vector;
	
	private BukkitTask task;
	
	private List<Runnable> onFinishListeners = new ArrayList<Runnable>();
	
	private SoulParticles(Location from, Targetable to, long time, int density){
		this.world = from.getWorld();
		this.from = from;
		this.fromHandle = from.toVector().add(new Vector(0,5,0)).add(to.getLocation().toVector().subtract(from.toVector()).multiply(0.1));
		this.to = to;
		this.color = Color.fromRGB(112, 255, 200);
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
			this.spawnParticle(t);
			this.t+=this.step;
		}
	}
	
	protected void addOnHitListener(Runnable runnable){
		this.onFinishListeners.add(runnable);
	}
	
	private void spawnParticle(float t){
		this.targetLocation = this.to.getLocation();
		this.targetHandle = this.from.toVector().midpoint(this.targetLocation.toVector()).add(new Vector(0,5,0));
		this.spline = new Spline3D(this.from.toVector(), this.fromHandle, this.targetHandle, this.targetLocation.toVector());
		this.vector = this.spline.getPositionAt(t*3);
		this.world.spawnParticle(Particle.REDSTONE, this.vector.getX(),this.vector.getY(),this.vector.getZ(),0,Math.max(this.color.getRed()/255f, Float.MIN_VALUE),this.color.getGreen()/255f,this.color.getBlue()/255f,1);
	}
	
	protected static SoulParticles spawn(Location from, Targetable to, float speed){
		long time = Mathf.roundToInt(from.distance(to.getLocation())*(1/(Mathf.clamp(speed,0.1,100))));
		SoulParticles result = new SoulParticles(from,to, time, 4);
		result.task = Bukkit.getScheduler().runTaskTimer(HalloweenEventPlugin.getInstance(), result, 0, 1);
		return result;
	}
}
