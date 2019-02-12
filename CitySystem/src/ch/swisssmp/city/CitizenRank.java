package ch.swisssmp.city;

public enum CitizenRank {
	MAYOR,
	FOUNDER,
	CITIZEN;
	
	public String getDisplayName(){
		switch(this){
		case MAYOR: return "Bürgermeister";
		case FOUNDER: return "Gründer";
		case CITIZEN: return "Bürger";
		default: return null;
		}
	}
	
	public static CitizenRank get(String value){
		try{
			return CitizenRank.valueOf(value);
		}
		catch(Exception e){
			return null;
		}
	}
}
