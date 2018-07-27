package ch.swisssmp.loot.populator;

import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

public class FurnacePopulator extends InventoryPopulator{
	
	public FurnacePopulator(FurnaceInventory inventory, ItemStack[] templateStacks, String seed){
		super(inventory, templateStacks, seed);
	}
	
	@Override
	protected void fillContainer(){
		FurnaceInventory furnaceInventory = (FurnaceInventory) this.inventory;
		furnaceInventory.clear();
		for(ItemStack itemStack : this.itemStacks){
			if(itemStack.getType().isFuel() && furnaceInventory.getFuel()==null){
				furnaceInventory.setFuel(itemStack);
			}
			else if(furnaceInventory.getResult()==null){
				furnaceInventory.setResult(itemStack);
			}
			else if(furnaceInventory.getItem(0)==null){
				furnaceInventory.setItem(0, itemStack);
			}
		}
	}
}
