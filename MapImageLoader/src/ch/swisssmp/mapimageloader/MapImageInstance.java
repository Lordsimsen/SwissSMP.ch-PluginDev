package ch.swisssmp.mapimageloader;

import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

public class MapImageInstance {
	private final UUID imageUid;
	private int x;
	private int y;
	
	public MapImageInstance(UUID imageUid) {
		this(imageUid, 0, 0);
	}
	
	public MapImageInstance(UUID imageUid, int x, int y) {
		this.imageUid = imageUid;
		this.x = x;
		this.y = y;
	}
	
	public UUID getImageUid() {
		return this.imageUid;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	protected void save(ConfigurationSection dataSection) {
		dataSection.set("x", this.x);
		dataSection.set("y", this.y);
	}
	
	protected void load(ConfigurationSection dataSection) {
		this.x = dataSection.getInt("x");
		this.y = dataSection.getInt("y");
	}
}
