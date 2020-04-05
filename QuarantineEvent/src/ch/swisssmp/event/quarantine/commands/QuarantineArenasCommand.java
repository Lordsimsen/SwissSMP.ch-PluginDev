package ch.swisssmp.event.quarantine.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.event.quarantine.QuarantineArenasView;
import ch.swisssmp.event.quarantine.QuarantineEventPlugin;

public class QuarantineArenasCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		String prefix = QuarantineEventPlugin.getPrefix();
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(prefix+ChatColor.RED+" Kann nur ingame verwendet werden.");
			return true;
		}
		
		QuarantineArenasView.open((Player) sender);
		
		return true;
	}
}
