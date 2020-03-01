package ch.swisssmp.livemap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import ch.swisssmp.livemap.markers.AreaMarker;
import ch.swisssmp.livemap.markers.CircleMarker;
import ch.swisssmp.livemap.markers.MarkerIcon;
import ch.swisssmp.livemap.markers.PolyLineMarker;
import ch.swisssmp.livemap.markers.PositionMarker;

public abstract class Livemap {
	private static Livemap instance;
	
	protected abstract PositionMarker _getPositionMarker(World world, String group_id, String marker_id);
	protected abstract AreaMarker _getAreaMarker(World world, String group_id, String marker_id);
	protected abstract PolyLineMarker _getPolyLineMarker(World world, String group_id, String marker_id);
	protected abstract CircleMarker _getCircleMarker(World world, String group_id, String marker_id);
	protected abstract MarkerIcon _getIcon(String icon_id);
	
	protected abstract void _saveMarker(PositionMarker marker);
	protected abstract void _saveMarker(AreaMarker marker);
	protected abstract void _saveMarker(PolyLineMarker marker);
	protected abstract void _saveMarker(CircleMarker marker);
	protected abstract void _saveIcon(MarkerIcon icon);
	
	protected static void link(){
		PluginManager pluginManager = Bukkit.getPluginManager();
		Plugin dynmap = pluginManager.getPlugin("dynmap");
		Livemap result;
		if(dynmap!=null) result = DynmapHandler.create(dynmap);
		else result = null;
		if(result!=null){
			instance = result;
		}
	}
	
	public static PositionMarker getPositionMarker(World world, String group_id, String marker_id){
		return instance!=null ? instance._getPositionMarker(world, group_id, marker_id) : null;
	}
	
	public static AreaMarker getAreaMarker(World world, String group_id, String marker_id){
		return instance!=null ? instance._getAreaMarker(world, group_id, marker_id) : null;
	}
	
	public static PolyLineMarker getPolyLineMarker(World world, String group_id, String marker_id){
		return instance!=null ? instance._getPolyLineMarker(world, group_id, marker_id) : null;
	}
	
	public static CircleMarker getCircleMarker(World world, String group_id, String marker_id){
		return instance!=null ? instance._getCircleMarker(world, group_id, marker_id) : null;
	}
	
	public static void saveMarker(PositionMarker marker){
		if(instance==null) return;
		instance._saveMarker(marker);
	}
	
	public static void saveMarker(AreaMarker marker){
		if(instance==null) return;
		instance._saveMarker(marker);
	}
	
	public static void saveMarker(PolyLineMarker marker){
		if(instance==null) return;
		instance._saveMarker(marker);
	}
	
	public static void saveMarker(CircleMarker marker){
		if(instance==null) return;
		instance._saveMarker(marker);
	}
	
	public static MarkerIcon getIcon(String icon_id){
		return instance!=null ? instance._getIcon(icon_id) : null;
	}
	
	public static void saveIcon(MarkerIcon icon){
		if(instance==null) return;
		instance._saveIcon(icon);
	}
}
