package ch.swisssmp.craftmmo.mmoquest;

import org.bukkit.ChatColor;

public enum MmoQuestObjectiveType {
VARIOUS,
HUNT,
GATHER,
DISCOVER,
PROTECT,
SIEGE,
ACTION
;
	public static MmoQuestObjectiveType get(String typeString){
		if(typeString==null){
			return null;
		}
		switch(typeString){
		case "VARIOUS":
			return VARIOUS;
		case "HUNT":
			return HUNT;
		case "GATHER":
			return GATHER;
		case "DISCOVER":
			return DISCOVER;
		case "PROTECT":
			return PROTECT;
		case "SIEGE":
			return SIEGE;
		case "ACTION":
			return ACTION;
		default:
			return null;
		}
	}
	
	public ChatColor getColor(){
		switch(this){
		case VARIOUS:
			return ChatColor.DARK_GREEN;
		case HUNT:
			return ChatColor.DARK_RED;
		case GATHER:
			return ChatColor.GREEN;
		case DISCOVER:
			return ChatColor.GOLD;
		case PROTECT:
			return ChatColor.BLUE;
		case SIEGE:
			return ChatColor.DARK_PURPLE;
		case ACTION:
			return ChatColor.DARK_GRAY;
		default:
			return ChatColor.RESET;
		}
	}
}
