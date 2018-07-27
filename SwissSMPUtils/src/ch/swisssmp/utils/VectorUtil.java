package ch.swisssmp.utils;

import org.bukkit.util.Vector;

public class VectorUtil {
	public static boolean isBetween(Vector vector, Vector from, Vector to){
		return Mathf.isBetween(vector.getX(), from.getX(), to.getX()) &&
				Mathf.isBetween(vector.getY(), from.getY(), to.getY()) &&
				Mathf.isBetween(vector.getZ(), from.getZ(), to.getZ());
	}
}
