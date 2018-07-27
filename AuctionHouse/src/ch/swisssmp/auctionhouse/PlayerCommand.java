package ch.swisssmp.auctionhouse;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.webcore.DataSource;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length<1) return false;
		ArrayList<String> parameters = new ArrayList<String>();
		String[] parametersArray;
		switch(args[0]){
			case "starttimed":{
				if(args.length<3) return false;
				parameters.add("start="+URLEncoder.encode(args[1]));
				parameters.add("end="+URLEncoder.encode(args[2]));
				if(args.length>3) parameters.add("starttime="+URLEncoder.encode(args[3]));
				if(args.length>4) parameters.add("endtime="+URLEncoder.encode(args[4]));
				if(sender instanceof Player)parameters.add("initializer="+((Player)sender).getUniqueId());
				else parameters.add("initializer=console");
				parametersArray = new String[parameters.size()];
				String message = DataSource.getResponse("auction/start.php", parameters.toArray(parametersArray));
				sender.sendMessage(message);
				break;
			}
			case "start":{
				if(args.length>1) parameters.add("end="+URLEncoder.encode(args[1]));
				if(args.length>2) parameters.add("endtime="+URLEncoder.encode(args[2]));
				if(sender instanceof Player)parameters.add("initializer="+((Player)sender).getUniqueId());
				else parameters.add("initializer=console");
				parametersArray = new String[parameters.size()];
				String message = DataSource.getResponse("auction/start.php", parameters.toArray(parametersArray));
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

