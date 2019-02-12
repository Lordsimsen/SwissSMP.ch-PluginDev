package ch.swisssmp.warehouse;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.bukkit.World;
import org.bukkit.util.BlockVector;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.world.WorldManager;

public class ChestCollection<T extends ChestItem> {
	
	private final World world;
	protected final HashMap<UUID,T> items = new HashMap<UUID,T>();
	protected final HashMap<BlockVector,UUID> chests = new HashMap<BlockVector,UUID>();
	
	protected ChestCollection(World world){
		this.world = world;
	}
	
	public World getWorld(){
		return world;
	}
	
	public T getItem(BlockVector block){
		UUID item_id = chests.get(block);
		if(item_id==null) return null;
		return items.get(item_id);
	}
	
	public T getItem(UUID item_id){
		return items.get(item_id);
	}
	
	public Collection<T> getItems(){
		return items.values();
	}
	
	protected void registerChest(BlockVector blockVector, UUID item_id){
		chests.put(blockVector, item_id);
	}
	
	protected void unregisterChest(BlockVector blockVector){
		chests.remove(blockVector);
	}
	
	protected void add(T item){
		items.put(item.getId(), item);
		for(BlockVector chest : item.getChests()){
			chests.put(chest, item.getId());
		}
	}
	
	protected void remove(ChestItem item){
		items.remove(item.getId());
		for(BlockVector chest : item.getChests()){
			chests.remove(chest);
		}
	}
	
	protected static File getSaveFile(World world, String filename){
		return new File(WorldManager.getPluginDirectory(WarehousesPlugin.getInstance(), world),filename);
	}
	
	public void save(String filename){
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		ConfigurationSection warehousesSection = yamlConfiguration.createSection("chests");
		for(ChestItem item : items.values()){
			ConfigurationSection dataSection = warehousesSection.createSection(item.getId().toString());
			item.save(dataSection);
		}
		File file = getSaveFile(world, filename);
		File directory = file.getParentFile();
		if(!directory.exists()){
			directory.mkdirs();
		}
		yamlConfiguration.save(file);
	}

	protected void load(World world, String filename, Callable<T> makeone){
		File file = getSaveFile(world, filename);
		if(!file.exists()) return;
		YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
		if(yamlConfiguration==null) return;
		ConfigurationSection chestsSection = yamlConfiguration.getConfigurationSection("chests");
		try {
			for(String key : chestsSection.getKeys(false)){
				ConfigurationSection itemSection = chestsSection.getConfigurationSection(key);
				T item = makeone.call();
				item.load(this, itemSection);
				if(!item.isValid()) continue;
				for(BlockVector chest : item.getChests()){
					this.chests.put(chest, item.getId());
				}
				items.put(item.getId(), item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
