package ch.swisssmp.zones;

public enum MemberRole {
	JUNIOR("Junior"),
	MEMBER("Mitglied"),
	OWNER("Besitzer");
	
	private final String name;
	
	private MemberRole(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public static MemberRole get(String arg0){
		try{
			return MemberRole.valueOf(arg0.toUpperCase());
		}
		catch(Exception e){
			return null;
		}
	}
}
