package ch.swisssmp.zvierigame.game;

import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

public interface BrewAction {
    public void brew(BrewerInventory inventory, ItemStack itemStack, ItemStack ingredient);
}
