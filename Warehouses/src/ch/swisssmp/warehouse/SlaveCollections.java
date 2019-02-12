package ch.swisssmp.warehouse;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class SlaveCollections {
	private static HashMap<World,ChestCollection<Slave>> collections = new HashMap<World,ChestCollection<Slave>>();
	
	protected static ChestCollection<Slave> getCollection(World world){
		return collections.get(world);
	}
	
	protected static void saveCollection(World world){
		ChestCollection<Slave> collection = getCollection(world);
		if(collection==null) return;
		collection.save("slaves.yml");
	}
	
	protected static void unloadCollection(World world, boolean save){
		if(!collections.containsKey(world)) return;
		if(save){
			saveCollection(world);
		}
		collections.remove(world);
	}
	
	protected static void addCollection(World world, ChestCollection<Slave> collection){
		collections.put(world, collection);
	}
	
	protected static ChestCollection<Slave> loadCollection(World world){
		ChestCollection<Slave> result = new ChestCollection<Slave>(world);
		result.load(world, "slaves.yml", ()->{ return new Slave(); });
		collections.put(world, result);
		return result;
	}
	
	protected static void loadAll(){
		for(World world : Bukkit.getWorlds()){
			loadCollection(world);
		}
	}
}
