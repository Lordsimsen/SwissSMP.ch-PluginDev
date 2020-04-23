package ch.swisssmp.knightstournament;

public enum LanceColor {
	
	WHITE,
	ORANGE,
	MAGENTA,
	LIGHT_BLUE,
	YELLOW,
	LIME,
	PINK,
	GRAY,
	LIGHT_GRAY,
	CYAN,
	BLUE,
	PURPLE,
	GREEN,
	BROWN,
	RED,
	BLACK,
	NONE
	;
	
	public static LanceColor of(String s) {
		try {
			return LanceColor.valueOf(s);
		} catch (Exception e) {
			return null;
		}
	}

}
