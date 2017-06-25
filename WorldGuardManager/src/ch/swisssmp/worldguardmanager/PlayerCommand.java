package ch.swisssmp.worldguardmanager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length<1) return false;
		switch(args[0]){
		case "reload":{
			WorldGuardManager.plugin.UpdateWorldGuardInfos();
			sender.sendMessage("[WorldGuardManager] Regionen aktualisiert.");
			break;
		}
		default:{
			break;
		}
		}
		return true;
	}

}
