package ch.swisssmp.utils;

import org.bukkit.util.Vector;

public class VectorKey{
	
	private final Vector vector;
	private final int hashCode;
	
	public VectorKey(Vector vector){
		this.vector = vector;
		this.hashCode = (this.vector.getX()+","+this.vector.getY()+","+this.vector.getZ()).hashCode();
	}
	
	public Vector getVector(){
		return this.vector;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof VectorKey)) return false;
		VectorKey key = (VectorKey)o;
		return key.getVector().getX()==this.getVector().getX() && key.getVector().getY()==this.getVector().getY() && key.getVector().getZ()==this.getVector().getZ();
	}

	@Override
	public int hashCode(){
		return this.hashCode;
	}
}
