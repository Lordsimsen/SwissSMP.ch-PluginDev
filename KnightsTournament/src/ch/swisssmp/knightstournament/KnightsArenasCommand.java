package ch.swisssmp.knightstournament;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KnightsArenasCommand implements CommandExecutor{
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Nur ingame zu gebrauchen");
			return true;
		}
		Player p = (Player) sender;
		
		if(args.length != 0) return false;
		
//		boolean showAll = true;
//		for(String flag : args) {
//			switch(flag.toLowerCase()) {
//			case "-welt":
//			case "-world":{
//				showAll = false;
//				break;
//			}
//			default: p.sendMessage(KnightsTournamentPlugin.prefix + "Unbekannte Flag" + flag); //Sendets immer, odr?
//			}
//		}	
		KnightsArenasEditor.open(p);
		return true;
	}
}