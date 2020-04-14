package ch.swisssmp.customitems;

import org.bukkit.inventory.ItemStack;

public interface CustomItemBuilderModifier {
	void apply(ItemStack itemStack);
}
