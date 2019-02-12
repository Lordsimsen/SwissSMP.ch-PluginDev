package ch.swisssmp.warehouse.filters;

public enum FilterSetting {
	Default,
	Any,
	Include,
	Exclude
	;
	
	public static FilterSetting get(String input){
		if(input==null) return null;
		switch(input.toLowerCase()){
		case "include": return Include;
		case "exclude": return Exclude;
		case "any": return Any;
		case "default":
		default: return Default;
		}
	}
}
