package ch.swisssmp.utils;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class RandomizedLocation {
	private Random random = new Random();
	private final Location location;
	private final double range;
	
	public RandomizedLocation(Location location, double range){
		if(location==null) throw new NullPointerException("Cannot create a randomized location without a location!");
		this.location = location;
		this.range = range;
	}
	
	/**
	 * 
	 * @return returns a random location within the given range
	 */
	public Location getLocation(){
		return getLocation(true);
	}
	
	/**
	 * 
	 * @param randomized sets whether to give a randomized or this object's original location
	 * @return returns a location with given parameters
	 */
	public Location getLocation(boolean randomized){
		return getLocation(randomized, randomized, randomized);
	}
	
	/**
	 * 
	 * @param randomized_x sets whether to randomize the x axis
	 * @param randomized_z sets whether to randomize the z axis
	 * @return returns a location with given parameters where randomized_y is TRUE by default
	 */
	public Location getLocation(boolean randomized_x, boolean randomized_z){
		return getLocation(randomized_x, false, randomized_z);
	}
	
	/**
	 * 
	 * @param randomized_x sets whether to randomize the x axis
	 * @param randomized_y sets whether to randomize the y axis
	 * @param randomized_z sets whether to randomize the z axis
	 * @return returns a location with given parameters
	 */
	public Location getLocation(boolean randomized_x, boolean randomized_y, boolean randomized_z){
		Vector vector = location.toVector();
		if(randomized_x){
			vector.setX(randomizeValue(vector.getX(), range));
		}
		if(randomized_x){
			vector.setX(randomizeValue(vector.getX(), range));
		}
		if(randomized_x){
			vector.setX(randomizeValue(vector.getX(), range));
		}
		return new Location(this.location.getWorld(), vector.getX(), vector.getY(), vector.getZ());
	}
	
	private double randomizeValue(double value, double range){
		return value-range+2*random.nextDouble()*range;
	}
}
