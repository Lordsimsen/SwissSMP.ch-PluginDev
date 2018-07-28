package ch.swisssmp.utils;

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
	public static double lerp(double from, double to, double t){
		return from+(to-from)*t;
	}
	/**
	 * Returns whether the given value is between from and to (inclusive)
	 */
	public static boolean isBetween(double value, double from, double to){
		return value>=from && value<=to;
	}
}
