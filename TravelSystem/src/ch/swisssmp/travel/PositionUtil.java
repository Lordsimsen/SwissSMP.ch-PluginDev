package ch.swisssmp.travel;

import org.bukkit.util.Vector;

import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.Position;

public class PositionUtil {
	public static Position transform(Position position, Position a, Position b){
		float yawDelta = (Mathf.roundToInt(b.getYaw()/90) - Mathf.roundToInt(a.getYaw()/90))*90;
		double yawRadians = Math.toRadians(yawDelta);
		Vector vector = new Vector(position.getX(), position.getY(), position.getZ());
		Vector from = new Vector(Mathf.floorToInt(a.getX())+0.5, Mathf.floorToInt(a.getY())+0.5, Mathf.floorToInt(a.getZ())+0.5);
		Vector to = new Vector(Mathf.floorToInt(b.getX())+0.5, Mathf.floorToInt(b.getY())+0.5, Mathf.floorToInt(b.getZ())+0.5);
		Vector positionDelta = vector.subtract(from);
		double ca = Math.cos(yawRadians);
		double sa = Math.sin(yawRadians);
		Vector positionDeltaRotated = new Vector(ca*positionDelta.getX() - sa*positionDelta.getZ(), positionDelta.getY(), sa*positionDelta.getX() + ca*positionDelta.getZ());
		Vector result = to.add(positionDeltaRotated);
		return new Position(result.getX(), result.getY(), result.getZ(), position.getYaw()+yawDelta, position.getPitch());
	}
}
