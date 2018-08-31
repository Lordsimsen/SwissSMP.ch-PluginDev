package ch.swisssmp.loginrewards;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		switch(args[0]){
		case "trigger":{
			for(Player player : Bukkit.getOnlinePlayers()){
				LoginRewards.trigger(player);
			}
			if(sender instanceof Player){
				sender.sendMessage("[LoginRewards] Belohnungen verteilt.");
			}
			return true;
		}
		case "reset":{
			LoginRewards.reset();
			if(sender instanceof Player){
				sender.sendMessage("[LoginRewards] Wöchentliche Zähler zurückgesetzt.");
			}
		}
		default: return false;
		}
	}

}
