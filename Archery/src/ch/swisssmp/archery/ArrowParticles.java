package ch.swisssmp.archery;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.scheduler.BukkitTask;

public class ArrowParticles implements Runnable{
	private BukkitTask task;
	private Arrow arrow;
	private float r;
	private float g;
	private float b;
	
	private World world;
	private Location location;
	
	public ArrowParticles(Arrow arrow, Color color){
		this.arrow = arrow;
		this.r = Math.max(color.getRed()/255f,Float.MIN_VALUE); //if red is zero the particle is rendered as red only (wait what...)
		this.g = color.getGreen()/255f;
		this.b = color.getBlue()/255f;
		this.world = arrow.getWorld();
	}
	
	@Override
	public void run() {
		location = arrow.getLocation();
		world.spawnParticle(Particle.REDSTONE, location.getX(),location.getY(),location.getZ(),0,r,g,b,1);
		if((!this.arrow.isValid() || this.arrow.isOnGround()) && this.task!=null){
			this.task.cancel();
		}
	}
	
	public void setTask(BukkitTask task){
		this.task = task;
	}
}
