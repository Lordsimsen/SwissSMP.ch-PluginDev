package ch.swisssmp.knightstournament;

import org.bukkit.Material;

public enum LanceColor {

	RED,
	BLUE,
	GREEN,
	YELLOW,
	CYAN,
	PURPLE,
	ORANGE,
	LIME,
	LIGHT_BLUE,
	PINK,
	MAGENTA,
	BLACK,
	GRAY,
	LIGHT_GRAY,
	BROWN,
	WHITE,
	NONE
	;

	public static LanceColor of(Material material){
		switch(material){
			case WHITE_DYE: return WHITE;
			case ORANGE_DYE: return ORANGE;
			case MAGENTA_DYE: return MAGENTA;
			case LIGHT_BLUE_DYE: return LIGHT_BLUE;
			case YELLOW_DYE: return YELLOW;
			case LIME_DYE: return LIME;
			case PINK_DYE: return PINK;
			case GRAY_DYE: return GRAY;
			case LIGHT_GRAY_DYE: return LIGHT_GRAY;
			case CYAN_DYE: return CYAN;
			case BLUE_DYE: return BLUE;
			case PURPLE_DYE: return PURPLE;
			case GREEN_DYE: return GREEN;
			case BROWN_DYE: return BROWN;
			case RED_DYE: return RED;
			case BLACK_DYE: return BLACK;
			default: return null;
		}
	}
	
	public static LanceColor of(String s) {
		try {
			return LanceColor.valueOf(s);
		} catch (Exception e) {
			return null;
		}
	}

}
