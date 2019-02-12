package ch.swisssmp.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;

public class WaypointAPI extends JavaPlugin {

	private static PluginDescriptionFile pdfFile;
	private static WaypointAPI plugin;
	
	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		
		Bukkit.getPluginManager().registerEvents(new EventListener(), plugin);
		Bukkit.getPluginCommand("waypoint").setExecutor(new WaypointCommand());
		
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static ItemStack getMissingItem(){
		return WaypointAPI.getMissingItem(ChatColor.RED+"Fehlender Wegpunkt");
	}
	
	public static ItemStack getMissingItem(String displayName){
		CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder("MARKER_MISSING");
		if(itemBuilder==null) return null;
		itemBuilder.setDisplayName(displayName);
		itemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemBuilder.setLore(getWaypointMissingLore());
		return itemBuilder.build();
	}
	
	public static ItemStack getItem(Position position){
		return WaypointAPI.getItem(position, MarkerType.RED);
	}
	
	public static ItemStack getItem(Position position, MarkerType color){
		return WaypointAPI.getItem(position, color, ChatColor.RESET+"Wegpunkt");
	}
	
	public static ItemStack getItem(Position position, MarkerType color, String displayName){
		return WaypointAPI.getItem(position, color, displayName, true);
	}
	
	public static ItemStack getItem(Position position, MarkerType color, String displayName, boolean addInstructions){
		if(position==null) position = new Position(0,64,0,0,0);
		
		String customEnum = "MARKER_"+color;
		CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder(customEnum);
		if(itemBuilder==null) return null;
		itemBuilder.setDisplayName(displayName);
		itemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemBuilder.setLore(getWaypointLore(position, addInstructions));
		ItemStack itemStack = itemBuilder.build();
		ItemUtil.setPosition(itemStack, "waypoint", position);
		return itemStack;
	}
	
	public static Position getPosition(ItemStack waypointStack){
		return ItemUtil.getPosition(waypointStack, "waypoint");
	}
	
	public static void setAttachedWorld(ItemStack waypointStack, World world){
		ItemUtil.setString(waypointStack, "attached_world", world.getName());
	}
	
	public static World getAttachedWorld(ItemStack waypointStack){
		String attachedWorld = ItemUtil.getString(waypointStack, "attached_world");
		if(attachedWorld==null) return null;
		return Bukkit.getWorld(attachedWorld);
	}
	
	public static WaypointAPI getInstance(){
		return plugin;
	}
	
	public static List<String> getWaypointMissingLore(){
		List<String> result = new ArrayList<String>();
		result.add(ChatColor.YELLOW+"Mit /wegpunkt eine Position");
		result.add(ChatColor.YELLOW+"speichern und danach hier einfügen");
		return result;
	}
	
	public static List<String> getWaypointLore(Position position, boolean addInstructions){
		List<String> result = new ArrayList<String>();
		result.add(ChatColor.GRAY+"X: "+Mathf.round(position.getX(),1)+", Y: "+Mathf.round(position.getY(),1)+", Z: "+Mathf.round(position.getZ(),1));
		if(addInstructions){
			result.add(ChatColor.GRAY+"Linksklick: Teleport");
			result.add(ChatColor.GRAY+"Rechtsklick: Punkt setzen");
		}
		return result;
	}
}
