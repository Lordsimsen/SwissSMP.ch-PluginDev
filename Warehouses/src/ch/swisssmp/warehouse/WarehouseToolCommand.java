package ch.swisssmp.warehouse;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarehouseToolCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return true;
		Player player = (Player) sender;
		player.getInventory().addItem(ItemManager.getWarehouseTool());
		return true;
	}

}
