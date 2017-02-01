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
			WebCore.loadYamls();
			sender.sendMessage(ChatColor.DARK_GRAY+"Konfiguration neu geladen.");
			break;
		case "debug":
			WebCore.debug = !WebCore.debug;
			if(WebCore.debug){
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
