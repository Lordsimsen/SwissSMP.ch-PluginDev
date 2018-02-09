package ch.swisssmp.world;

import ch.swisssmp.utils.ConfigurationSection;

public class WorldBorder {
	private final int center_x;
	private final int center_z;
	private final int radius;
	private final boolean wrap;
	private final int margin;
	
	public WorldBorder(int center_x, int center_z, int radius, boolean wrap, int margin){
		this.center_x = center_x;
		this.center_z = center_z;
		this.radius = radius;
		this.wrap = wrap;
		this.margin = margin;
	}
	
	public static WorldBorder create(ConfigurationSection dataSection){
		int center_x = dataSection.getInt("center_x");
		int center_z = dataSection.getInt("center_z");
		int radius = dataSection.getInt("radius");
		boolean wrap = dataSection.getBoolean("wrap");
		int margin = dataSection.getInt("margin");
		return new WorldBorder(center_x, center_z, radius, wrap, margin);
	}
	
	public int getCenterX(){
		return this.center_x;
	}
	public int getCenterZ(){
		return this.center_z;
	}
	public int getRadius(){
		return this.radius;
	}
	public boolean doWrap(){
		return this.wrap;
	}
	public int getMargin(){
		return this.margin;
	}
}
