package ch.swisssmp.utils;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class RandomizedLocation {
	private Random random = new Random();
	private final World world;
	private final double x;
	private final double y;
	private final double z;
	private final float yaw;
	private final float pitch;
	private final double range;
	
	public RandomizedLocation(Location location, double range){
		if(location==null) throw new NullPointerException("Cannot create a randomized location without a location!");
		this.world = location.getWorld();
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.yaw = location.getYaw();
		this.pitch = location.getPitch();
		this.range = range;
	}

	public RandomizedLocation(World world, double x, double y, double z, double range){
		this(world,x,y,z,0.5f,0,range);
	}
	
	public RandomizedLocation(World world, double x, double y, double z, float yaw, float pitch, double range){
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
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
	 * @param world sets the world for the returned location
	 * @return returns a location with given parameters
	 */
	public Location getLocation(World world){
		return getLocation(world, true);
	}
	
	/**
	 * 
	 * @param world sets the world for the returned location
	 * @param randomized sets whether to give a randomized or this object's original location
	 * @return returns a location with given parameters
	 */
	public Location getLocation(World world, boolean randomized){
		return getLocation(world, randomized, randomized, randomized);
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
	 * @param world sets the world for the returned location
	 * @param randomized_x sets whether to randomize the x axis
	 * @param randomized_z sets whether to randomize the z axis
	 * @return returns a location with given parameters where randomized_y is TRUE by default
	 */
	public Location getLocation(World world, boolean randomized_x, boolean randomized_z){
		return getLocation(world, randomized_x, false, randomized_z);
	}
	
	/**
	 * 
	 * @param randomized_x sets whether to randomize the x axis
	 * @param randomized_y sets whether to randomize the y axis
	 * @param randomized_z sets whether to randomize the z axis
	 * @return returns a location with given parameters
	 */
	public Location getLocation(boolean randomized_x, boolean randomized_y, boolean randomized_z){
		return getLocation(this.world, randomized_x, randomized_y, randomized_z);
	}
	
	/**
	 * 
	 * @param world sets the world for the returned location
	 * @param randomized_x sets whether to randomize the x axis
	 * @param randomized_y sets whether to randomize the y axis
	 * @param randomized_z sets whether to randomize the z axis
	 * @return returns a location with given parameters
	 */
	public Location getLocation(World world, boolean randomized_x, boolean randomized_y, boolean randomized_z){
		if(world==null) return null;
		Vector vector = new Vector(x,y,z);
		if(randomized_x){
			vector.setX(randomizeValue(vector.getX(), range));
		}
		if(randomized_x){
			vector.setX(randomizeValue(vector.getX(), range));
		}
		if(randomized_x){
			vector.setX(randomizeValue(vector.getX(), range));
		}
		return new Location(world, vector.getX(), vector.getY(), vector.getZ(), this.yaw, this.pitch);
	}
	
	private double randomizeValue(double value, double range){
		return value-range+2*random.nextDouble()*range;
	}
}
