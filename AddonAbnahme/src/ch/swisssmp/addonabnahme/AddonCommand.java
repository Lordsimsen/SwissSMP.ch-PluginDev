package ch.swisssmp.addonabnahme;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AddonCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		switch(args[0]){
		case "reload":
			AddonInstanceGuides.updateAll();
			sender.sendMessage(AddonAbnahme.getPrefix()+ChatColor.GREEN+"Addon Guides aktualisiert.");
			return true;
		default: return false;
		}
	}

}
