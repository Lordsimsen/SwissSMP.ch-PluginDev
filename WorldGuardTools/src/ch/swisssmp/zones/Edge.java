package ch.swisssmp.zones;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import ch.swisssmp.utils.Mathf;

public class Edge {
	
	private final Location a;
	private final Location b;
	private final Vector delta;
	private final Vector direction;
	
	private double length;
	private double lengthSquared;
	
	public Edge(Location a, Location b){
		this.a = a;
		this.b = b;
		this.delta = b.toVector().subtract(a.toVector());
		this.direction = this.delta.clone().normalize();
		this.length = delta.length();
		this.lengthSquared = delta.lengthSquared();
	}
	
	public Location getA(){
		return a;
	}
	
	public Location getB(){
		return b;
	}
	
	public Vector getDelta(){
		return delta;
	}
	
	public Vector getDirection(){
		return direction;
	}
	
	public double getLength(){
		return length;
	}
	
	public double getLengthSquared(){
		return lengthSquared;
	}
	
	public double getDistanceSquared(Location location){
		Vector ab = b.toVector().subtract(a.toVector());
		
        double line_dist = (ab).lengthSquared();
        if (line_dist == 0) return a.clone().subtract(location).lengthSquared();
        //project Point onto edge
        double t = location.clone().subtract(a).toVector().dot(ab)/line_dist;
        //Dot Product can be negative or longer than AB so we clamp it to the edge length
        t = Mathf.clamp01(t);
        Location pointOnEdge = a .clone().add(ab.multiply(t));
        return (location.clone().subtract(pointOnEdge).lengthSquared());
	}
	
	public Location interpolate(float t){
		return a.clone().add(delta.clone().multiply(t));
	}
	
	public Location step(double length){
		return a.clone().add(this.direction.clone().multiply(length));
	}
}
