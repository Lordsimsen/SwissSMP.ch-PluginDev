package ch.swisssmp.crafting.brewing;

import org.bukkit.inventory.ItemStack;

public interface BrewingFilter {
    boolean isMatch(ItemStack resultBase);
}
