package ch.swisssmp.utils;

import org.bukkit.util.Vector;

public final class Mathf{
	/**
	 * Returns  value clamped between min and max (inclusive)
	 */
	public static double clamp(double value, double min, double max){
		return Math.max(Math.min(value, max), min);
	}
	/**
	 * Returns  value clamped between 0 and 1 (inclusive)
	 */
	public static double clamp01(double value){
		return Mathf.clamp(value, 0, 1);
	}
	/**
	 * Returns  value clamped between min (inclusive) and max (exclusive)
	 */
	public static int clamp(int value, int min, int max){
		return Math.max(Math.min(value, max-1), min);
	}
	public static int floorToInt(double value){
		return (int)Math.floor(value);
	}
	public static int ceilToInt(double value){
		return (int)Math.ceil(value);
	}
	public static int roundToInt(double value){
		return (int)Math.round(value);
	}
	public static float round(float value, int decimal_places){
		int power_of_ten = (int)Math.pow(10, decimal_places);
		return Math.round(value*power_of_ten)/power_of_ten;
	}
	public static double round(double value, int decimal_places){
		int power_of_ten = (int)Math.pow(10, decimal_places);
		return Math.round(value*power_of_ten)/power_of_ten;
	}
	public static double lerp(double from, double to, double t){
		return from+(to-from)*t;
	}
	/**
	 * Returns whether the given value is between from and to (inclusive)
	 */
	public static boolean isBetween(double value, double from, double to){
		return value>=from && value<=to;
	}
	/**
	 * Calculates the viewing Vector based on pitch and yaw
	 * @param yaw - The rotation on the y-axis (expects value between 0-360)
	 * @param pitch - The rotation on the x-axis (expects value between 0-180)
	 * @return A normalized Vector pointing in the same direction
	 */
	public static Vector getDirection(float yaw, float pitch){
		double yawRadians = Math.toRadians(yaw);
		double pitchRadians = Math.toRadians(pitch);
		double xzLen = Math.cos(pitchRadians);
		double x = xzLen * Math.cos(yawRadians);
		double y = Math.sin(pitchRadians);
		double z = xzLen * Math.sin(-yawRadians);
		return new Vector(x,y,z);
	}
	/**
	 * @param a - Start of the line
	 * @param b - End of the line
	 * @param c - The point to calculate the distance to
	 * @return The squared perpendicular distance from the line between <code>a</code> and <code>b</code> to <code>c</code>
	 */
	public static double getPointDistance(Vector a, Vector b, Vector c){
		double line_dist = b.clone().subtract(a).lengthSquared();
		if(line_dist==0) return (a.clone().subtract(c)).lengthSquared();
		double t = (c.clone().subtract(a)).dot(b.clone().subtract(a))/line_dist;
		return c.clone().subtract((b.clone().subtract(a).multiply(t)).add(a)).lengthSquared();
	}

	/**
	 * @param value - The value to wrap
	 * @param step - The limit to wrap
	 * @return The wrapped value between 0 (inclusive) and <code>step</code> (exclusive)
	 */
	public static int wrap(int value, int step){
		while(value<0){
			value+=step;
		}
		while(value>=step){
			value-=step;
		}
		return value;
	}
}
