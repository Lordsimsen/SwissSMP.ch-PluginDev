package ch.swisssmp.loot;

import org.bukkit.ChatColor;

public enum LootType {
TREASURE,RARE,BASIC,JUNK;
	public String getCustomEnum(){
		switch(this){
		case TREASURE: return "TREASURE_CHEST";
		case RARE: return "RARE_CHEST";
		case BASIC: return "BASIC_CHEST";
		case JUNK: return "JUNK_CHEST";
		default: return null;
		}
	}
	public ChatColor getColor(){
		switch(this){
		case TREASURE: return ChatColor.LIGHT_PURPLE;
		case RARE: return ChatColor.GOLD;
		case BASIC: return ChatColor.GREEN;
		case JUNK: return ChatColor.GRAY;
		default: return ChatColor.WHITE;
		}
	}
	public static LootType getByName(String input){
		if(input==null)return null;
		switch(input.toUpperCase()){
		case "SCHATZ":
		case "TREASURE": return TREASURE;
		case "SELTEN":
		case "RARE": return RARE;
		case "NORMAL":
		case "BASIC": return BASIC;
		case "MÜLL":
		case "ABFALL":
		case "HÄUFIG":
		case "JUNK": return JUNK;
		default: return null;
		}
	}
}
