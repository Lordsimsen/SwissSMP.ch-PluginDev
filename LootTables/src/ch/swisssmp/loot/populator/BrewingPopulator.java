package ch.swisssmp.loot.populator;

import org.bukkit.Material;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

public class BrewingPopulator extends InventoryPopulator{
	
	public BrewingPopulator(BrewerInventory inventory, ItemStack[] templateStacks, String seed){
		super(inventory,templateStacks, seed);
	}
	
	@Override
	protected void fillContainer(){
		BrewerInventory brewerInventory = (BrewerInventory)this.inventory;
		brewerInventory.clear();
		int potionIndex = 0;
		for(ItemStack itemStack : this.itemStacks){
			if(itemStack.getItemMeta() instanceof PotionMeta){
				if(potionIndex<3){
					brewerInventory.setItem(potionIndex, itemStack);
					potionIndex++;
				}
			}
			else if(itemStack.getType()==Material.BLAZE_POWDER){
				if(brewerInventory.getFuel()==null){
					brewerInventory.setFuel(itemStack);
				}
			}
			else{
				if(brewerInventory.getIngredient()==null){
					brewerInventory.setIngredient(itemStack);
				}
			}
		}
	}
}
