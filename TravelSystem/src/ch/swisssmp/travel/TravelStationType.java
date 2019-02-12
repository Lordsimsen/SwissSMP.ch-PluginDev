package ch.swisssmp.travel;

public enum TravelStationType {
	AIRSHIP("Luftschiff", "TRAVELSTATION_AIRSHIP"),
	SHIP("Schiff", "TRAVELSTATION_SHIP"),
	TRAIN("Eisenbahn", "TRAVELSTATION_TRAIN"),
	AIRPLANE("Flugzeug", "TRAVELSTATION_AIRPLANE");
	
	private final String name;
	private final String icon;
	
	private TravelStationType(String name, String icon){
		this.name = name;
		this.icon = icon;
	}
	
	public String getIcon(){
		return icon;
	}
	
	public String getName(){
		return name;
	}
	
	public static TravelStationType getByType(String name){
		try{return TravelStationType.valueOf(name);}catch(Exception e){return null;}
	}
	
	public TravelStationType getNext(){
		switch(this){
		case AIRSHIP: return SHIP;
		case SHIP: return TRAIN;
		case TRAIN: return AIRPLANE;
		case AIRPLANE: return AIRSHIP;
		default: return AIRSHIP;
		}
	}
	
	public TravelStationType getPrevious(){
		switch(this){
		case AIRSHIP: return AIRPLANE;
		case SHIP: return AIRSHIP;
		case TRAIN: return SHIP;
		case AIRPLANE: return TRAIN;
		default: return AIRSHIP;
		}
	}
}
