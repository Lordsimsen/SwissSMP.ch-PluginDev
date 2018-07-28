package ch.swisssmp.utils;

import org.bukkit.util.Vector;

public class VectorUtil {
	/**
	 * Returns whether a Vector is between from and to (inclusive)
	 */
	public static boolean isBetween(Vector vector, Vector from, Vector to){
		return Mathf.isBetween(vector.getX(), from.getX(), to.getX()) &&
				Mathf.isBetween(vector.getY(), from.getY(), to.getY()) &&
				Mathf.isBetween(vector.getZ(), from.getZ(), to.getZ());
	}
	
	/**
	 * Rotates a Vector by a given amount of rotationSteps (90 degrees increment)
	 */
	public static Vector rotate(Vector vector, int rotationSteps){
		switch(rotationSteps%4){
		case 1: return new Vector(-vector.getZ(),vector.getY(),vector.getX());
		case 2: return new Vector(-vector.getX(),vector.getY(),-vector.getZ());
		case 3: return new Vector(vector.getZ(),vector.getY(),-vector.getX());
		default: return vector;
		}
	}
}
