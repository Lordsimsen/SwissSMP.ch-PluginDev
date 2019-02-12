package ch.swisssmp.shops;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		switch(args[0]){
		case "create":{
			if(!(sender instanceof Player)) return true;
			Player player = (Player) sender;
			Shop.create(player.getLocation().add(0, 0, 0), player);
			sender.sendMessage(ShopsPlugin.getPrefix()+ChatColor.GREEN+"Shop erstellt.");
			break;
		}
		default: return false;
		}
		return true;
	}

}
