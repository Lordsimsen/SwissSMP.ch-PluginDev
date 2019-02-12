package ch.swisssmp.addonabnahme;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum AddonState {
	Blocked("Blockiert", ChatColor.DARK_RED, Material.RED_WOOL),
	Unavailable("Nicht verfügbar", ChatColor.GRAY, Material.GRAY_WOOL),
	Available("Verfügbar", ChatColor.GREEN, Material.LIME_WOOL),
	Activated("Aktiviert", ChatColor.AQUA, Material.CYAN_WOOL),
	Accepted("Freigeschaltet", ChatColor.GREEN, Material.GREEN_WOOL),
	Examination("In Prüfung", ChatColor.BLUE, Material.BLUE_WOOL)
	;
	
	private final String display_name;
	private final ChatColor color;
	private final Material material;
	
	private AddonState(String display_name, ChatColor color, Material material){
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
			AddonState result = AddonState.valueOf(key);
			return result;
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
