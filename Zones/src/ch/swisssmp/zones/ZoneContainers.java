package ch.swisssmp.zones;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.World;

public class ZoneContainers {
	private final static HashMap<World,ZoneContainer> containers = new HashMap<World,ZoneContainer>();
	
	protected static ZoneContainer get(World world){
		return containers.get(world);
	}
	
	public static Collection<ZoneContainer> getAll(){
		return containers.values();
	}
	
	protected static void add(World world, ZoneContainer zoneContainer){
		containers.put(world, zoneContainer);
	}
	
	protected static void remove(World world){
		containers.remove(world);
	}
	
	protected static void clear(){
		containers.clear();
	}
}
