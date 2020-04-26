package ch.swisssmp.utils;

import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import com.google.gson.JsonObject;

public class VectorUtil {
	public static JsonObject serialize(EulerAngle euler) {
		JsonObject result = new JsonObject();
		result.addProperty("x", euler.getX());
		result.addProperty("y", euler.getX());
		result.addProperty("z", euler.getX());
		return result;
	}
	
	public static EulerAngle deserializeEuler(JsonObject data) {
		double x = data.get("x").getAsDouble();
		double y = data.get("y").getAsDouble();
		double z = data.get("z").getAsDouble();
		return new EulerAngle(x,y,z);
	}
	
	public static JsonObject serialize(Vector v) {
		JsonObject result = new JsonObject();
		result.addProperty("x", v.getX());
		result.addProperty("y", v.getX());
		result.addProperty("z", v.getX());
		return result;
	}
	
	public static Vector deserializeVector(JsonObject data) {
		double x = data.get("x").getAsDouble();
		double y = data.get("y").getAsDouble();
		double z = data.get("z").getAsDouble();
		return new Vector(x,y,z);
	}
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

	/**
	 * Rotates a Vector by a given amount of euler degrees on x axis
	 */
	public static Vector rotateX(Vector vector, float euler){
		return vector.rotateAroundX(euler / 180 * Math.PI);
	}

	/**
	 * Rotates a Vector by a given amount of euler degrees on y axis
	 */
	public static Vector rotateY(Vector vector, float euler){
		return vector.rotateAroundY(euler / 180 * Math.PI);
	}

	/**
	 * Rotates a Vector by a given amount of euler degrees on z axis
	 */
	public static Vector rotateZ(Vector vector, float euler){
		return vector.rotateAroundZ(euler / 180 * Math.PI);
	}

	/**
	 * Rotates a Vector by a given amount of euler degrees on axis
	 */
	public static Vector rotate(Vector vector, Vector axis, float euler){
		return vector.rotateAroundAxis(axis, euler / 180 * Math.PI);
	}
}
