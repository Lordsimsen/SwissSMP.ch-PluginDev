package ch.swisssmp.ceremonies;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ISacrificeListener {
	void sacrifice(ItemStack itemStack, Player player);
}
