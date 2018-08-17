package ch.swisssmp.customentities;

import org.bukkit.Location;

public class CustomEntityBuilder {
	
	private final CustomEntityBlueprint blueprint;
	
	public CustomEntityBuilder(CustomEntityBlueprint blueprint){
		this.blueprint = blueprint;
	}
	
	public static CustomEntity build(Location location){
		
		return new CustomEntity();
	}
}
