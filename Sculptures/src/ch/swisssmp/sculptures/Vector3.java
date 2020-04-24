package ch.swisssmp.sculptures;

import org.bukkit.util.Vector;

public class Vector3 extends Vector {
	public static final Vector3 forward = new Vector3(0,0,1);
	public static final Vector3 up = new Vector3(0,1,0);
	public static final Vector3 right = new Vector3(1,0,0);
	public static final Vector3 left = new Vector3(-1,0,0);
	public static final Vector3 down = new Vector3(0,-1,0);
	public static final Vector3 back = new Vector3(0,0,-1);
	
	public Vector3(Vector v) {
		this(v.getX(),v.getY(),v.getZ());
	}
	
	public Vector3(double x, double y, double z) {
		super(x,y,z);
	}
}
