package ch.swisssmp.waypoints;

public enum MarkerType {
	RED,
	BLUE,
	MISSING;
	
	public static MarkerType getByName(String name){
		if(name==null) return null;

		try{
			return MarkerType.valueOf(name);
		}
		catch(Exception e){
			return null;
		}
	}
}
