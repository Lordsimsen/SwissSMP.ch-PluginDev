package ch.swisssmp.commandscheduler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null) return false;
		if(args.length<1) return false;
		switch(args[0]){
		case "run":{
			CommandScheduler.runCommands();
			sender.sendMessage(ChatColor.GREEN+"Executed all commands.");
			break;
		}
		case "toggle":{
			CommandScheduler.disable = !CommandScheduler.disable;
			if(CommandScheduler.disable){
				sender.sendMessage("Commands are not being executed anymore for now.");
			}
			else{
				sender.sendMessage("Commands are executed again.");
			}
		}
		}
		return true;
	}

}
