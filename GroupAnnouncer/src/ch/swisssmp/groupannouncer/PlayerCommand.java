package ch.swisssmp.groupannouncer;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length<1) return false;
		switch(args[0]){
		case "announce":{
			if(args.length<3){
				return false;
			}
			String filter = args[1];
			ArrayList<String> messageParts = new ArrayList<String>();
			for(int i = 2; i < args.length; i++){
				messageParts.add(args[i]);
			}
			String message = String.join(" ", messageParts).replace('$', '§');
			ArrayList<String> counters = GroupAnnouncer.announce(message,filter);
			sender.sendMessage("[GroupAnnouncer] Nachricht versendet. ("+String.join(", ", counters)+")");
			break;
		}
		case "colors":{
			String[] colors = new String[]{
					ChatColor.BLACK+"0",
					ChatColor.DARK_BLUE+"1",
					ChatColor.DARK_GREEN+"2",
					ChatColor.DARK_AQUA+"3",
					ChatColor.DARK_RED+"4",
					ChatColor.DARK_PURPLE+"5",
					ChatColor.GOLD+"6",
					ChatColor.GRAY+"7",
					ChatColor.DARK_GRAY+"8",
					ChatColor.BLUE+"9",
					ChatColor.GREEN+"a",
					ChatColor.AQUA+"b",
					ChatColor.RED+"c",
					ChatColor.LIGHT_PURPLE+"d",
					ChatColor.YELLOW+"e",
					ChatColor.WHITE+"f",
			};
			sender.sendMessage("[GroupAnnouncer] Farben: "+String.join(" ", colors));
			break;
		}
		}
		return true;
	}

}
