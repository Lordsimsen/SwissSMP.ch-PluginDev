package ch.swisssmp.dungeongenerator;

import java.util.Random;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

public class GeneratorPartMarker extends BukkitRunnable{
	private final Location location;
	private final Color color;
	private final Random random = new Random();
	private float lifetime = 60;
	
	private GeneratorPartMarker(Location location, Color color, long lifetime){
		this.location = location;
		this.color = color;
		this.runTaskTimer(DungeonGeneratorPlugin.plugin, 0, 3L);
		this.lifetime = lifetime;
	}
	@Override
	public void run(){
		this.location.getWorld().spawnParticle(Particle.REDSTONE, this.location.getX()+getRandomOffset(),this.location.getY()+getRandomOffset(),this.location.getZ()+getRandomOffset()*3,0,Math.max(this.color.getRed()/255f, Float.MIN_VALUE),this.color.getGreen()/255f,this.color.getBlue()/255f,1);
		this.lifetime-=1f;
		if(this.lifetime<=0){
			this.cancel();
		}
	}
	
	private float getRandomOffset(){
		return -0.05f+random.nextFloat()*0.1f;
	}
	
	public static GeneratorPartMarker show(Location location, Color color, long lifetime){
		return new GeneratorPartMarker(location, color, lifetime);
	}
}
