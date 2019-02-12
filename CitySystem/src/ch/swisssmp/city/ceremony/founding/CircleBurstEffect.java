package ch.swisssmp.city.ceremony.founding;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;

import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.utils.Random;

public class CircleBurstEffect {
	
	private final Location from;
	private final float size;
	private final Color colorA;
	private final Color colorB;
	Random random = new Random();
	
	private CircleBurstEffect(Location location, float size, Color colorA, Color colorB){
		this.from = location;
		this.size = size;
		this.colorA = colorA;
		this.colorB = colorB;
	}
	
	private void start(){
		this.spawnSparks();
	}
	
	private void spawnSparks(){
		for(int i = 0; i < 15; i++){
			float radius = i/15f*size;
			Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), ()->{
				spawnSparkRing(radius);
			}, i);
		}
	}
	
	private void spawnSparkRing(float radius){
		final int detail = 12;
		final float step = 1f/detail;
		for(int i = 0; i < detail; i++){
			float progress = step*i;
			double x = Math.cos((2*Math.PI)*progress) * radius;
			double z = Math.sin((2*Math.PI)*progress) * radius;
			DustOptions dust = new DustOptions(random.nextDouble()>0.3 ? colorA : colorB, 1);
			Location location = from.clone().add(x, 0, z);
			from.getWorld().spawnParticle(Particle.REDSTONE, location, 1, dust);
		}
	}
	
	public static CircleBurstEffect play(Block block, float size, Color colorA, Color colorB){
		String sound = size>3 ? "fire_burst_big" : "fire_burst_small";
		Location location = new Location(block.getWorld(),block.getX()+0.5,block.getY()+0.5,block.getZ()+0.5,0,-90);
		CircleBurstEffect result = new CircleBurstEffect(location,size,colorA,colorB);
		result.start();
		block.getWorld().playSound(location, sound, SoundCategory.BLOCKS, 1f, 15f);
		return result;
	}
}
