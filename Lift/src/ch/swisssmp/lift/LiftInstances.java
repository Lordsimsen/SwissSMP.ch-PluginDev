package ch.swisssmp.lift;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.World;

public class LiftInstances {
	private final static List<LiftInstance> instances = new ArrayList<LiftInstance>();
	
	public static void add(LiftInstance instance){
		instances.add(instance);
	}
	public static void remove(LiftInstance instance){
		instances.remove(instance);
	}
	
	public static void remove(World world){
		Collection<LiftInstance> queue = instances.stream().filter(l->l.getWorld()==world).collect(Collectors.toList());
		for(LiftInstance instance : queue){
			instances.remove(instance);
		}
	}
	
	public static List<LiftInstance> getAll(){
		return Collections.unmodifiableList(instances);
	}
}
