package ch.swisssmp.craftmmo.mmoattribute;

public enum MmoElement {
	NEUTRAL, 
	FIRE, EARTH, AIR, WATER, 
	LIGHT, DARKNESS;
	public static MmoElement get(String name){
		switch(name){
			case "FIRE":
				return MmoElement.FIRE;
			case "EARTH":
				return MmoElement.EARTH;
			case "AIR":
				return MmoElement.AIR;
			case "WATER":
				return MmoElement.WATER;
			case "LIGHT":
				return MmoElement.LIGHT;
			case "DARKNESS":
				return MmoElement.DARKNESS;
			default: return MmoElement.NEUTRAL;
		}
	}
	@Override
	public String toString(){
		switch(this){
		case FIRE:
			return "FIRE";
		case EARTH:
			return "EARTH";
		case AIR:
			return "AIR";
		case WATER:
			return "WATER";
		case LIGHT:
			return "LIGHT";
		case DARKNESS:
			return "DARKNESS";
		default: return "NEUTRAL";
		}
	}
}
