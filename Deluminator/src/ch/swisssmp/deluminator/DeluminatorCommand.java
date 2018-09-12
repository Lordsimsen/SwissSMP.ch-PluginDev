package ch.swisssmp.deluminator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeluminatorCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("[Deluminator] Kann nur ingame verwendet werden.");
			return true;
		}
		Player player = (Player) sender;
		player.getInventory().addItem(DeluminatorPlugin.getItem());
		return true;
	}

}
