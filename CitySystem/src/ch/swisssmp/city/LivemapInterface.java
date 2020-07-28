package ch.swisssmp.city;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import ch.swisssmp.livemap.Livemap;
import ch.swisssmp.livemap.LivemapMarkerAPI;
import ch.swisssmp.livemap.markers.MarkerIcon;
import ch.swisssmp.webcore.DataSource;

public class LivemapInterface {
	private static boolean linkActive = false;
	
	public static void updateAddonIcon(AddonType addon){
		if(!linkActive) return;
		MarkerIcon icon = Livemap.getIcon(addon.getIconId());
		if(icon==null){
			icon = new MarkerIcon(addon.getIconId());
		}
		icon.setFileUrl(DataSource.getBaseUrl()+"/"+addon.getLivemapIconUrl());
		icon.setLabel(addon.getName());
		Livemap.saveIcon(icon);
	}
	
	static void link(){
		linkActive = false;
		Plugin livemapAPI = Bukkit.getPluginManager().getPlugin("LivemapMarkerAPI");
		if(!(livemapAPI instanceof LivemapMarkerAPI)) return;
		linkActive = true;
	}
}
