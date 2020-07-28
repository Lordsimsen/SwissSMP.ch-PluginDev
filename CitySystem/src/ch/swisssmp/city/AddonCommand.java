package ch.swisssmp.city;

import ch.swisssmp.city.guides.AddonGuides;
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
			AddonGuides.updateAll();
			sender.sendMessage(CitySystemPlugin.getPrefix()+ChatColor.GREEN+"Addon Guides aktualisiert.");
			return true;
		default: return false;
		}
	}

}
