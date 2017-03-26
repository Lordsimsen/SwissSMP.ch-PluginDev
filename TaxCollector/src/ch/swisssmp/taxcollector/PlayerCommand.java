package ch.swisssmp.taxcollector;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player){
			Player player = (Player) sender;
			TaxCollector.inform_player(new String[]{
					"player="+player.getUniqueId().toString()	
			}, player);
		}
		else{
			sender.sendMessage("Can only be used from within the game.");
			return true;
		}
		return true;
	}

}
