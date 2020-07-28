package ch.swisssmp.city;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum AddonState {
	BLOCKED("Blockiert", ChatColor.DARK_RED, Material.RED_WOOL),
	UNAVAILABLE("Nicht verfügbar", ChatColor.GRAY, Material.GRAY_WOOL),
	AVAILABLE("Verfügbar", ChatColor.GREEN, Material.LIME_WOOL),
	ACTIVATED("Aktiviert", ChatColor.AQUA, Material.CYAN_WOOL),
	ACCEPTED("Freigeschaltet", ChatColor.GREEN, Material.GREEN_WOOL)
	;
	
	private final String display_name;
	private final ChatColor color;
	private final Material material;
	
	AddonState(String display_name, ChatColor color, Material material){
		this.display_name = display_name;
		this.color = color;
		this.material = material;
	}
	
	public String getDisplayName(){
		return this.display_name;
	}
	
	public ChatColor getColor(){
		return this.color;
	}
	
	public Material getMaterial(){
		return material;
	}
	
	public static AddonState get(String key){
		try{
			return AddonState.valueOf(key);
		}
		catch(Exception e){
			for(AddonState state : AddonState.values()){
				if(state.toString().toLowerCase().equals(key.toLowerCase())) return state;
				String displayString = state.getColor()+state.getDisplayName();
				if(displayString.equals(key)) return state;
			}
			return null;
		}
	}
}
