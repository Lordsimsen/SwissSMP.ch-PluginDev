package ch.swisssmp.utils;

import org.bukkit.util.Vector;

public class Random extends java.util.Random {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void setSeed(String seed){
		this.setSeed(seed.hashCode());
		for(int i = 0; i < 10; i++) this.nextDouble();
	}

	public void setSeed(byte[] bytes){
		this.setSeed((new String(bytes)).hashCode());
		for(int i = 0; i < 10; i++) this.nextDouble();
	}
	
	public Vector insideUnitSphere(){
		double u = this.nextDouble();
		double v = this.nextDouble();
		double theta = 2 * Math.PI * u;
		double phi = Math.acos(2 * v - 1);
		double x = (Math.sin(phi) * Math.cos(theta));
		double y = (Math.sin(phi) * Math.sin(theta));
		double z = (Math.cos(phi));
		return new Vector(x,y,z);
	}
}
