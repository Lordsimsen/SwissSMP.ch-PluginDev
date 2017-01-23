package ch.swisssmp.adventuredungeons.mmoitem;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MmoLootInventory implements Runnable{
	public static HashMap<Block, ArrayList<MmoLootInventory>> inventories = new HashMap<Block, ArrayList<MmoLootInventory>>();
	public static HashMap<Inventory, MmoLootInventory> inventoryMap = new HashMap<Inventory, MmoLootInventory>();
	
	public final Player player;
	public final String action;
	public final Block block;
	public final Inventory inventory;
	private final ArrayList<ItemStack> original_items;
	public final boolean global;
	
	public int task_id;
	
	private MmoLootInventory(Player player, String action, boolean global, Block block, Inventory inventory){
		this.player = player;
		this.action = action;
		this.block = block;
		this.inventory = inventory;
		if(!inventories.containsKey(this.block)){
			inventories.put(block, new ArrayList<MmoLootInventory>());
		}
		inventories.get(block).add(this);
		inventoryMap.put(this.inventory, this);
		original_items = new ArrayList<ItemStack>();
		for(ItemStack itemStack : inventory){
			if(itemStack==null)
				continue;
			original_items.add(itemStack);
		}
		this.global = global;
	}
	public static MmoLootInventory create(Player player, String action, boolean global, Block block, Inventory inventory){
		return new MmoLootInventory(player, action, global, block, inventory);
	}
	@Override
	public void run() {
		if(block.getWorld()!=null){
			close();
		}
	}
	public void close(){
		inventories.remove(this.block);
		inventoryMap.remove(this.inventory);
		World world = block.getWorld();
		for(ItemStack itemStack : inventory){
			if(itemStack!=null && !original_items.contains(itemStack)){
				world.dropItem(block.getLocation().add(0.5, 1.5, 0.5), itemStack);
			}
		}
		Bukkit.getScheduler().cancelTask(task_id);
	}
	
	public static MmoLootInventory get(Player player, String action, Block block){
		ArrayList<MmoLootInventory> inventoriesAtLocation = inventories.get(block);
		if(inventoriesAtLocation==null)
			return null;
		for(MmoLootInventory lootInventory : inventoriesAtLocation){
			if((lootInventory.player.getUniqueId()==player.getUniqueId() || lootInventory.global) && lootInventory.action.equals(action))
				return lootInventory;
		}
		return null;
	}
	
	public static MmoLootInventory get(Inventory inventory){
		return inventoryMap.get(inventory);
	}
}
