package ch.swisssmp.zvieriplausch;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ZvieriArenenCommand implements CommandExecutor{
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Nur ingame zu gebrauchen");
			return true;
		}
		Player p = (Player) sender;
		
		boolean showAll = true;

		for(String flag : args) {
			switch(flag.toLowerCase()) {
			case "-welt":
			case "-world":{
				showAll = false;
				break;
			}
			case "reload":{
				ZvieriArenen.load(p.getWorld());
				p.sendMessage(ZvieriGamePlugin.getPrefix() + " Reloading arenas");
				return true;
			}
			default: p.sendMessage("Ung�ltige flag");
			}
		}
		ZvieriArenenEditor.open(p, showAll);
		return true;
	}
}
