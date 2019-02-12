package ch.swisssmp.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class Position {
	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;
	
	public Position(double x, double y, double z){
		this(x,y,z,0,90);
	}

	public Position(double x, double y, double z, float yaw, float pitch){
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	public Position(Position position){
		this.x = position.x;
		this.y = position.y;
		this.z = position.z;
		this.yaw = position.yaw;
		this.pitch = position.pitch;
	}
	
	public Position(Location location){
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.yaw = location.getYaw();
		this.pitch = location.getPitch();
	}
	
	public double getX(){return this.x;}
	public int getBlockX(){return Mathf.floorToInt(x);}
	public double getY(){return this.y;}
	public int getBlockY(){return Mathf.floorToInt(y);}
	public double getZ(){return this.z;}
	public int getBlockZ(){return Mathf.floorToInt(z);}
	public float getYaw(){return this.yaw;}
	public float getPitch(){return this.pitch;}

	public Position setX(double x){this.x = x; return this;}
	public Position setY(double y){this.y = y; return this;}
	public Position setZ(double z){this.z = z; return this;}
	public Position setYaw(float yaw){this.yaw = yaw; return this;}
	public Position setPitch(float pitch){this.pitch = pitch; return this;}
	
	public Position add(Vector vector){this.x+=vector.getX();this.y+=vector.getY();this.z=vector.getZ();return this;}
	public Position add(double x, double y, double z){
		this.x+=x;
		this.y+=y;
		this.z+=z;
		return this;
	}
	
	public Location getLocation(World world){
		return new Location(world, this.x,this.y,this.z,this.yaw,this.pitch);
	}
	
	public Position clone(){
		return new Position(this);
	}
	
	public String getURLString(String parameterName){
		return parameterName+"[x]="+this.x+"&"
				+ parameterName+"[y]="+this.y+"&"
				+ parameterName+"[z]="+this.z+"&"
				+ parameterName+"[yaw]="+this.yaw+"&"
				+ parameterName+"[pitch]="+this.pitch;
	}
}
