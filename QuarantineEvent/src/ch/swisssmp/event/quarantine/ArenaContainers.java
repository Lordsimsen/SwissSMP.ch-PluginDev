package ch.swisssmp.event.quarantine;

import java.util.HashMap;

import org.bukkit.World;

public class ArenaContainers {
	private static HashMap<World,ArenaContainer> containers = new HashMap<World,ArenaContainer>();
	
	protected static ArenaContainer load(World world) {
		ArenaContainer container = ArenaContainer.load(world);
		if(container==null) container = new ArenaContainer(world);
		containers.put(world, container);
		return container;
	}
	
	protected static void unload(World world) {
		ArenaContainer container = getContainer(world);
		container.unload();
		containers.remove(world);
	}
	
	protected static void clear() {
		for(World world : containers.keySet()) {
			unload(world);
		}
	}
	
	protected static ArenaContainer getContainer(World world) {
		return containers.get(world);
	}
}
