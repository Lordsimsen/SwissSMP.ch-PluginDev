package ch.swisssmp.loot.populator;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DispenserPopulator extends GenericPopulator{
	
	public DispenserPopulator(Inventory inventory, ItemStack[] templateStacks, String seed){
		super(inventory,templateStacks, seed);
	}
}
