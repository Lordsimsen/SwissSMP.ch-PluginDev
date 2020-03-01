package ch.swisssmp.warehouse.filters;

public enum FilterSetting {
	Default,
	Any,
	Include,
	Exclude,
	Exact
	;
	
	public static FilterSetting get(String input){
		if(input==null) return Default;
		switch(input.toLowerCase()){
		case "include": return Include;
		case "exclude": return Exclude;
		case "any": return Any;
		case "exact": return Exact;
		case "default":
		default: return Default;
		}
	}
}
