package ch.swisssmp.mapimageloader;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.map.MapFont;
import org.bukkit.map.MinecraftFont;

public class MapTextInstance {
	
	private String value;
	private MapFont font;
	private int posX;
	private int posY;
	
	public MapTextInstance() {
		
	}
	
	public MapTextInstance(String value, MapFont font) {
		this(value, font, 0, 0);
	}
	
	public MapTextInstance(String value, MapFont font, int x, int y) {
		this.value = value;
		this.font = font;
		this.posX = x;
		this.posY = y;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public MapFont getFont() {
		return font;
	}
	
	public void setFont(MapFont font) {
		this.font = font;
	}
	
	public int getX() {
		return posX;
	}
	
	public void setX(int x) {
		posX = x;
	}
	
	public int getY() {
		return posY;
	}
	
	public void setY(int y) {
		posY = y;
	}
	
	public void save(ConfigurationSection dataSection) {
		dataSection.set("value", value);
		dataSection.set("x", posX);
		dataSection.set("y", posY);
	}
	
	public void load(ConfigurationSection dataSection) {
		value = dataSection.getString("value");
		font = MinecraftFont.Font;
		posX = dataSection.getInt("x");
		posY = dataSection.getInt("y");
	}
}
