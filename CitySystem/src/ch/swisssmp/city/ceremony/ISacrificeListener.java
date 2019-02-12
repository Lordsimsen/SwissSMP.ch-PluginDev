package ch.swisssmp.city.ceremony;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ISacrificeListener {
	void sacrifice(ItemStack itemStack, Player player);
}
