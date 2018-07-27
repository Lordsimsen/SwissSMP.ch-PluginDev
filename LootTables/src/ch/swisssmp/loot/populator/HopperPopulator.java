package ch.swisssmp.loot.populator;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HopperPopulator extends GenericPopulator{
	
	public HopperPopulator(Inventory inventory, ItemStack[] templateStacks, String seed){
		super(inventory, templateStacks, seed);
	}
}
