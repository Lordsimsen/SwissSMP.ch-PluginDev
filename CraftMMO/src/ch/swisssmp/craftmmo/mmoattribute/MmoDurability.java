package ch.swisssmp.craftmmo.mmoattribute;

import org.bukkit.ChatColor;

public enum MmoDurability {
RED,
ORANGE,
YELLOW,
GREEN,
BLUE,
WHITE,
PURPLE;
	public ChatColor getColor(){
		switch(this){
		case RED:
			return ChatColor.RED;
		case ORANGE:
			return ChatColor.GOLD;
		case YELLOW:
			return ChatColor.YELLOW;
		case GREEN:
			return ChatColor.GREEN;
		case BLUE:
			return ChatColor.BLUE;
		case WHITE:
			return ChatColor.WHITE;
		case PURPLE:
			return ChatColor.LIGHT_PURPLE;
		default:
			return ChatColor.RESET;
		}
	}
	public static MmoDurability[] getOrder(){
		return new MmoDurability[]{
			RED,
			ORANGE,
			YELLOW,
			GREEN,
			BLUE,
			WHITE,
			PURPLE
		};
	}
	public double getDamageModifier(){
		switch(this){
		case RED:
			return 0.1;
		case ORANGE:
			return 0.5;
		case YELLOW:
			return 0.8;
		case GREEN:
			return 1;
		case BLUE:
			return 1.05;
		case WHITE:
			return 1.1;
		case PURPLE:
			return 1.2;
		default:
			return 0;
		}
	}
}
