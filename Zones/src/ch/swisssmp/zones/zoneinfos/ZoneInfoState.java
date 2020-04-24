package ch.swisssmp.zones.zoneinfos;

public enum ZoneInfoState {
	PENDING,
	ACTIVE,
	INACTIVE,
	MISSING;
	
	public String getDescription(){
		switch(this){
		case PENDING: return "Zone noch nicht initiiert";
		case ACTIVE: return "Zone aktiv und geladen";
		case INACTIVE: return "Zone nicht geladen";
		case MISSING: return "Zone nicht vorhanden";
		default: return toString();
		}
	}
}
