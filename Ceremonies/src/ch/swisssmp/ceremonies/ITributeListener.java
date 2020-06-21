package ch.swisssmp.ceremonies;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ITributeListener {
	void payTribute(ItemStack itemStack, Player player);
}
