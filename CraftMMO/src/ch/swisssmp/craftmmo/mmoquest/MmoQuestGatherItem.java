package ch.swisssmp.craftmmo.mmoquest;

import org.bukkit.inventory.ItemStack;

public class MmoQuestGatherItem {
	public final ItemStack itemStack;
	public final int target_amount;
	public int current_amount = 0;
	
	public MmoQuestGatherItem(ItemStack itemStack, int target_amount){
		this.itemStack = itemStack;
		this.target_amount = target_amount;
	}
}
