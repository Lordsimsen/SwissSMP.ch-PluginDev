package ch.swisssmp.text.properties;

import org.bukkit.ChatColor;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.md_5.bungee.api.chat.BaseComponent;

public class ColorProperty implements IOptionalProperty {

	private Color value;
	
	private ColorProperty(ColorProperty template) {
		this.value = template.value;
	}
	
	public ColorProperty(Color value) {
		this.value = value;
	}
	
	@Override
	public String getKey() {
		return "color";
	}

	@Override
	public JsonElement serialize() {
		return new JsonPrimitive(value.serializedValue);
	}
	
	public enum Color{
		BLACK("black"),
		DARK_BLUE("dark_blue"),
		DARK_GREEN("dark_green"),
		DARK_AQUA("dark_aqua"),
		DARK_RED("dark_red"),
		DARK_PURPLE("dark_purple"),
		GOLD("gold"),
		GRAY("gray"),
		DARK_GRAY("dark_gray"),
		BLUE("blue"),
		GREEN("green"),
		AQUA("aqua"),
		RED("red"),
		LIGHT_PURPLE("light_purple"),
		YELLOW("yellow"),
		WHITE("white"),
		RESET("reset");
		
		public final String serializedValue;
		
		private Color(String serializedValue) {
			this.serializedValue = serializedValue;
		}
		
		public net.md_5.bungee.api.ChatColor toSpigot(){
			switch(this) {
			case BLACK: return net.md_5.bungee.api.ChatColor.BLACK;
			case DARK_BLUE: return net.md_5.bungee.api.ChatColor.DARK_BLUE;
			case DARK_GREEN: return net.md_5.bungee.api.ChatColor.DARK_GREEN;
			case DARK_AQUA: return net.md_5.bungee.api.ChatColor.DARK_AQUA;
			case DARK_RED: return net.md_5.bungee.api.ChatColor.DARK_RED;
			case DARK_PURPLE: return net.md_5.bungee.api.ChatColor.DARK_PURPLE;
			case GOLD: return net.md_5.bungee.api.ChatColor.GOLD;
			case GRAY: return net.md_5.bungee.api.ChatColor.GRAY;
			case DARK_GRAY: return net.md_5.bungee.api.ChatColor.DARK_GRAY;
			case BLUE: return net.md_5.bungee.api.ChatColor.BLUE;
			case GREEN: return net.md_5.bungee.api.ChatColor.GREEN;
			case AQUA: return net.md_5.bungee.api.ChatColor.AQUA;
			case RED: return net.md_5.bungee.api.ChatColor.RED;
			case LIGHT_PURPLE: return net.md_5.bungee.api.ChatColor.LIGHT_PURPLE;
			case YELLOW: return net.md_5.bungee.api.ChatColor.YELLOW;
			case WHITE: return net.md_5.bungee.api.ChatColor.WHITE;
			case RESET: return net.md_5.bungee.api.ChatColor.RESET;
			default: return null;
			}
		}
		
		public static Color valueOf(ChatColor c) {
			switch(c) {
			case BLACK: return Color.BLACK;
			case DARK_BLUE: return Color.DARK_BLUE;
			case DARK_GREEN: return Color.DARK_GREEN;
			case DARK_AQUA: return Color.DARK_AQUA;
			case DARK_RED: return Color.DARK_RED;
			case DARK_PURPLE: return Color.DARK_PURPLE;
			case GOLD: return Color.GOLD;
			case GRAY: return Color.GRAY;
			case DARK_GRAY: return Color.DARK_GRAY;
			case BLUE: return Color.BLUE;
			case GREEN: return Color.GREEN;
			case AQUA: return Color.AQUA;
			case RED: return Color.RED;
			case LIGHT_PURPLE: return Color.LIGHT_PURPLE;
			case YELLOW: return Color.YELLOW;
			case WHITE: return Color.WHITE;
			case RESET: return Color.RESET;
			default: 
				System.out.println("Use the appropriate property for the attribute "+c.name());
				return null;
			}
		}
	}

	@Override
	public IProperty duplicate() {
		return new ColorProperty(this);
	}

	@Override
	public void applySpigotValues(BaseComponent component) {
		component.setColor(this.value.toSpigot());
	}
}
