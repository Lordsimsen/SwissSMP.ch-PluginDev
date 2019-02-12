package ch.swisssmp.world.border;

import org.bukkit.Location;
import org.bukkit.World;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.Mathf;

public class WorldBorder {
	private int center_x;
	private int center_z;
	private int radius;
	private boolean wrap;
	private int margin;
	
	private WorldBorder(int center_x, int center_z, int radius, boolean wrap, int margin){
		this.center_x = center_x;
		this.center_z = center_z;
		this.radius = radius;
		this.wrap = wrap;
		this.margin = margin;
	}
	
	public int getCenterX(){
		return this.center_x;
	}
	
	public void setCenterX(int center_x){
		this.center_x = center_x;
	}
	
	public int getCenterZ(){
		return this.center_z;
	}
	
	public void setCenterZ(int center_z){
		this.center_z = center_z;
	}
	
	public int getRadius(){
		return this.radius;
	}
	
	public void setRadius(int radius){
		this.radius = radius;
	}
	
	public boolean doWrap(){
		return this.wrap;
	}
	
	public void setDoWrap(boolean wrap){
		this.wrap = wrap;
	}
	
	public int getMargin(){
		return this.margin;
	}
	
	public void setMargin(int margin){
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
	
	public static WorldBorder create(World world){
		Location center = world.getWorldBorder().getCenter();
		double size = world.getWorldBorder().getSize();
		return new WorldBorder(center.getBlockX(),center.getBlockZ(),Mathf.roundToInt(size/2),false,50);
	}
}
