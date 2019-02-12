package ch.swisssmp.warehouse;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;

public class ChestUtility {
	public static DoubleChestInventory getDoubleChestInventory(Block block){
		if(!(block.getState() instanceof Chest)) return null;
		Chest chest = (Chest) block.getState();
		Inventory inventory = chest.getInventory();
		if(!(inventory instanceof DoubleChestInventory)) return null;
		return (DoubleChestInventory) inventory;
	}
	
	public static DoubleChest getDoubleChest(Block block){
		DoubleChestInventory doubleChestInventory = getDoubleChestInventory(block);
		if(doubleChestInventory==null) return null;
		return doubleChestInventory.getHolder();
	}
}
