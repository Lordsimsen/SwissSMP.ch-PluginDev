package ch.swisssmp.webcore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PlayerCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null) return false;
		if(args.length<1) return false;
		switch(args[0]){
		case "reload":
			Main.loadYamls();
			sender.sendMessage(ChatColor.DARK_GRAY+"Konfiguration neu geladen.");
			break;
		case "debug":
			Main.debug = !Main.debug;
			if(Main.debug){
				sender.sendMessage(ChatColor.GREEN+"Der Debug-Modus wurde aktiviert.");
			}
			else{
				sender.sendMessage(ChatColor.RED+"Der Debug-Modus wurde deaktiviert.");
			}
			break;
		}
		return true;
	}
}
