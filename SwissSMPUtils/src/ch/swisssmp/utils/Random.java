package ch.swisssmp.utils;

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
}
