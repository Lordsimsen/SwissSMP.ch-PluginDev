package ch.swisssmp.elytrarace;

import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.bukkit.ChatColor;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("Can only be executed from within the game!");
			return true;
		}
		Player player = (Player) sender;
		switch(label){
			case "spielen":
			case "play":{
				Main.preparePlayerPlay(player);
				break;
			}
			case "zuschauen":
			case "spectate":
			{
				Main.preparePlayerSpectate(player);
				PlayerRace race = Main.races.get(player.getUniqueId());
				if(race!=null) race.cancel();
				break;
			}
			case "rangliste":
			case "ranking":
			{
				Map<UUID, Long> bestTimes = Main.sortByValue(Main.highscores);
				int rank = 1;
				for(Entry<UUID, Long> entry : bestTimes.entrySet()){
					Player other = Bukkit.getPlayer(entry.getKey());
					if(other==null) continue;
					String playerName = other.getDisplayName();
					player.sendMessage(rank+". "+playerName+" - "+PlayerRace.formatTime(entry.getValue()));
					rank++;
				}
				break;
			}
			case "reset":
			{
				if(args!=null && args.length>0 && Bukkit.getPlayer(args[0])!=null){
					Player other = Bukkit.getPlayer(args[0]);
					PlayerRace race = Main.races.get(other.getUniqueId());
					if(race!=null) race.cancel();
					Main.highscores.remove(other.getUniqueId());
					player.sendMessage(ChatColor.YELLOW+"Erfolg: Highscore von "+other.getName()+" zurückgesetzt.");
				}
				else{
					for(PlayerRace race : Main.races.values()){
						race.cancel();
					}
					Main.races.clear();
					Main.highscores.clear();
					player.sendMessage(ChatColor.YELLOW+"Erfolg: Alle Highscores zurückgesetzt");
				}
				break;
			}
			default:
				break;
		}
		return true;
	}

}
