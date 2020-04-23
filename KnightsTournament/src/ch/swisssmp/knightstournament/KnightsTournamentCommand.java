package ch.swisssmp.knightstournament;

import java.util.Optional;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KnightsTournamentCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		switch(args[0]){
		case "open":{
			if(!(sender instanceof Player)){
				sender.sendMessage("Can only be used from within the game.");
				return true;
			}
			if(args.length<2) return false;
			Player player = (Player) sender;
			Optional<KnightsArena> arenaQuery = KnightsArena.get(player.getWorld(), args[1]);
			if(!arenaQuery.isPresent()){
				sender.sendMessage(KnightsTournamentPlugin.prefix+" Arena "+args[1]+" nicht gefunden.");
				return true;
			}
			Tournament.initialize(arenaQuery.get(), player);
			return true;
		}
		case "begin":{
			if(!(sender instanceof Player)){
				sender.sendMessage("Can only be used from within the game.");
				return true;
			}
			Player player = (Player) sender;
			Tournament tournament = Tournament.get(player);
			if(tournament==null){
				sender.sendMessage(KnightsTournamentPlugin.prefix+" Du leitest momentan kein Turnier.");
				return true;
			}
			tournament.start();
			return true;
		}
		case "end":{
			if(!(sender instanceof Player)){
				sender.sendMessage("Can only be used from within the game.");
				return true;
			}
			Player player = (Player) sender;
			Tournament tournament = Tournament.get(player);
			if(tournament==null){
				sender.sendMessage(KnightsTournamentPlugin.prefix+" Du leitest momentan kein Turnier.");
				return true;
			}
			tournament.finish();
			return true;
		}
		default:
			return false;
		}
	}

}
