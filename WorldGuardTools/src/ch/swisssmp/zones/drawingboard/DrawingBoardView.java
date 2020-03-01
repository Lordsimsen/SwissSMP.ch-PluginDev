package ch.swisssmp.zones.drawingboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.SwissSMPler;

public class DrawingBoardView {
	
	public static InventoryView open(Player player, ItemStack itemStack){
		if(!player.hasPermission("zones.drawingboard.use")){
			SwissSMPler.get(player).sendActionBar(ChatColor.WHITE+"Noch nicht freigeschaltet.");
			return null;
		}
		return DrawingBoardAssignZoneView.open(player);
	}
}
