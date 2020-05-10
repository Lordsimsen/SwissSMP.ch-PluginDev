package ch.swisssmp.crafting.brewing;

import org.bukkit.inventory.ItemStack;

public interface BrewFilter {
    boolean isMatch(ItemStack resultBase);
}
