package ch.swisssmp.warehouse;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class MasterCollections {
	private static HashMap<World,ChestCollection<Master>> collections = new HashMap<World,ChestCollection<Master>>();
	
	protected static ChestCollection<Master> getCollection(World world){
		return collections.get(world);
	}
	
	protected static void saveCollection(World world){
		ChestCollection<Master> collection = getCollection(world);
		if(collection==null) return;
		collection.save("masters.yml");
	}
	
	protected static void unloadCollection(World world, boolean save){
		if(!collections.containsKey(world)) return;
		if(save){
			saveCollection(world);
		}
		collections.remove(world);
	}
	
	protected static void addCollection(World world, ChestCollection<Master> collection){
		collections.put(world, collection);
	}
	
	protected static ChestCollection<Master> loadCollection(World world){
		ChestCollection<Master> result = new ChestCollection<Master>(world);
		result.load(world, "masters.yml", ()->{ return new Master(); });
		collections.put(world, result);
		return result;
	}
	
	protected static void loadAll(){
		for(World world : Bukkit.getWorlds()){
			loadCollection(world);
		}
	}
}
