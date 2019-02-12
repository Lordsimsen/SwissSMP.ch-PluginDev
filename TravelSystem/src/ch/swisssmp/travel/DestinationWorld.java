package ch.swisssmp.travel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.Mathf;
import ch.swisssmp.world.WorldManager;

class DestinationWorld {
	
	private final World world;
	private final List<TravelStation> stations = new ArrayList<TravelStation>();
	
	DestinationWorld(World world){
		this.world = world;
	}
	
	public void addStation(TravelStation station){
		this.stations.add(station);
	}
	
	public World getWorld(){
		return world;
	}
	
	public ItemStack getItem(){
		String display = this.getDisplayEnum(this.world.getEnvironment());
		CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder(display);
		itemBuilder.setDisplayName(ChatColor.AQUA+WorldManager.getDisplayName(world));
		itemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemBuilder.setLore(Arrays.asList(ChatColor.GRAY+"Wähle rechts",ChatColor.GRAY+"eine Station"));
		return itemBuilder.build();
	}
	
	public Collection<TravelStation> getStations(){
		stations.sort(new Comparator<TravelStation>() {
	         @Override
	         public int compare(TravelStation a, TravelStation b) {
	                 return a.getName().compareToIgnoreCase(b.getName());
	         }
	     });
		return stations;
	}
	
	public int getStationCount(){
		return stations.size();
	}
	
	public int getRowCount(){
		return Math.max(Mathf.ceilToInt((stations.size())/7f),1);
	}
	
	private String getDisplayEnum(Environment environment){
		switch(environment){
		case NORMAL: return "WORLD_OVERWORLD";
		case NETHER: return "WORLD_NETHER";
		case THE_END: return "WORLD_THE_END";
		default: return "WORLD_OVERWORLD";
		}
	}
}
