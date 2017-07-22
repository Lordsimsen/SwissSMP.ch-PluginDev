package ch.swisssmp.knightstournament;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		switch(args[0]){
		case "open":{
			if(!(sender instanceof Player)){
				sender.sendMessage("Can only be used from within the game.");
				return true;
			}
			if(args.length<3) return false;
			Player player = (Player) sender;
			KnightsArena arena = KnightsArena.get(args[1]);
			int maxParticipants = Integer.parseInt(args[2]);
			if(arena==null){
				sender.sendMessage(KnightsTournament.prefix+" Arena "+args[1]+" nicht gefunden.");
				return true;
			}
			Tournament.initialize(arena, player, maxParticipants);
			break;
		}
		case "begin":{
			if(!(sender instanceof Player)){
				sender.sendMessage("Can only be used from within the game.");
				return true;
			}
			Player player = (Player) sender;
			Tournament tournament = Tournament.get(player);
			if(tournament==null){
				sender.sendMessage(KnightsTournament.prefix+" Du leitest momentan kein Turnier.");
				return true;
			}
			tournament.start();
			break;
		}
		case "end":{
			if(!(sender instanceof Player)){
				sender.sendMessage("Can only be used from within the game.");
				return true;
			}
			Player player = (Player) sender;
			Tournament tournament = Tournament.get(player);
			if(tournament==null){
				sender.sendMessage(KnightsTournament.prefix+" Du leitest momentan kein Turnier.");
				return true;
			}
			tournament.finish();
			break;
		}
		}
		return true;
	}

}
