package ch.swisssmp.auctionhouse;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ch.swisssmp.webcore.DataSource;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length<1) return false;
		switch(args[0]){
		case "start":{
			String message = DataSource.getResponse("auction/start.php");
			sender.sendMessage(message);
			break;
		}
		case "end":{
			String message = DataSource.getResponse("auction/end.php");
			sender.sendMessage(message);
			break;
		}
		case "cancel":{
			String message = DataSource.getResponse("auction/cancel.php");
			sender.sendMessage(message);
			break;
		}
		}
		return true;
	}

}
