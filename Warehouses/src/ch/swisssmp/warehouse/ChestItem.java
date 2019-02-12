package ch.swisssmp.warehouse;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.util.BlockVector;

import ch.swisssmp.utils.ConfigurationSection;

public abstract class ChestItem {
	
	private ChestCollection<?> collection;
	private UUID item_id;
	
	private HashSet<BlockVector> chests = new HashSet<BlockVector>();
	
	protected ChestItem(){
		
	}
	
	public ChestCollection<?> getCollection(){
		return collection;
	}
	
	protected void setCollection(ChestCollection<?> collection){
		this.collection = collection;
	}
	
	public UUID getId(){
		return item_id;
	}
	
	protected void setId(UUID item_id){
		this.item_id = item_id;
	}
	
	public HashSet<BlockVector> getChests(){
		return new HashSet<BlockVector>(chests);
	}
	
	public boolean addChest(BlockVector vector){
		Block block = this.getCollection().getWorld().getBlockAt(vector.getBlockX(),vector.getBlockY(),vector.getBlockZ());
		return addChest(block);
	}
	
	public boolean addChest(Block block){
		if(block.getWorld()!=collection.getWorld()){
			Bukkit.getLogger().info("[Warehouse] Cannot add chest: "+block.getWorld().getName()+" is not the same World as "+collection.getWorld().getName()+"!");
			return false;
		}
		if(block.getWorld()!=collection.getWorld()) return false;
		DoubleChest doubleChest = ChestUtility.getDoubleChest(block);
		if(doubleChest==null){
			BlockVector blockVector = new BlockVector(block.getX(),block.getY(),block.getZ());
			collection.registerChest(blockVector,item_id);
			chests.add(blockVector);
		}
		else{
			BlockState left = (BlockState) doubleChest.getLeftSide();
			BlockState right = (BlockState) doubleChest.getRightSide();
			BlockVector leftVector = new BlockVector(left.getX(),left.getY(),left.getZ());
			collection.registerChest(leftVector,item_id);
			if(!chests.contains(leftVector)) chests.add(leftVector);
			BlockVector rightVector = new BlockVector(right.getX(),right.getY(),right.getZ());
			collection.registerChest(rightVector,item_id);
			if(!chests.contains(rightVector)) chests.add(rightVector);
		}
		return true;
	}
	
	public void removeChest(BlockVector vector, boolean removeConnected){
		Block block = this.getCollection().getWorld().getBlockAt(vector.getBlockX(),vector.getBlockY(),vector.getBlockZ());
		removeChest(block,removeConnected);
	}
	
	public void removeChest(Block block, boolean removeConnected){
		DoubleChest doubleChest = ChestUtility.getDoubleChest(block);
		if(doubleChest==null || !removeConnected){
			BlockVector blockVector = new BlockVector(block.getX(),block.getY(),block.getZ());
			collection.unregisterChest(blockVector);
			chests.remove(blockVector);
		}
		else{
			BlockState left = (BlockState) doubleChest.getLeftSide();
			BlockState right = (BlockState) doubleChest.getRightSide();
			BlockVector leftVector = new BlockVector(left.getX(),left.getY(),left.getZ());
			collection.unregisterChest(leftVector);
			chests.remove(leftVector);
			BlockVector rightVector = new BlockVector(right.getX(),right.getY(),right.getZ());
			collection.unregisterChest(rightVector);
			chests.remove(rightVector);
		}
		if(chests.size()==0){
			this.remove(new BlockVector(block.getX(),block.getY(),block.getZ()));
		}
	}
	
	public void remove(BlockVector lastChest){
		collection.remove(this);
	}
	
	protected void save(ConfigurationSection dataSection){
		dataSection.set("id", item_id.toString());
		ConfigurationSection chestsSection = dataSection.createSection("chests");
		int index = 0;
		for(BlockVector chest : this.chests){
			ConfigurationSection chestSection = chestsSection.createSection("chest_"+index);
			chestSection.set("x", chest.getX());
			chestSection.set("y", chest.getY());
			chestSection.set("z", chest.getZ());
			index++;
		}
		saveData(dataSection);
	}
	
	protected void load(ChestCollection<?> collection, ConfigurationSection dataSection){
		this.collection = collection;
		this.item_id = UUID.fromString(dataSection.getString("id"));
		ConfigurationSection chestsSection = dataSection.getConfigurationSection("chests");
		if(chestsSection!=null){
			for(String key : chestsSection.getKeys(false)){
				ConfigurationSection chestSection = chestsSection.getConfigurationSection(key);
				BlockVector vector = new BlockVector(chestSection.getInt("x"),chestSection.getInt("y"),chestSection.getInt("z"));
				this.chests.add(vector);
			}
		}
		this.loadData(dataSection);
	}
	
	public boolean isValid(){
		ChestCollection<?> collection = this.getCollection();
		World world = collection.getWorld();
		for(BlockVector chest : this.getChests()){
			Block block = world.getBlockAt(chest.getBlockX(), chest.getBlockY(), chest.getBlockZ());
			if(block.getType()!=Material.CHEST && block.getType()!=Material.TRAPPED_CHEST){
				this.chests.remove(chest);
				continue;
			}
			ChestItem existing = collection.getItem(chest);
			if(existing!=null && existing!=this){
				this.chests.remove(chest);
				continue;
			}
		}
		return this.chests.size()>0;
	}
	
	protected abstract void saveData(ConfigurationSection dataSection);
	protected abstract void loadData(ConfigurationSection dataSection);
}
