package ch.swisssmp.ageofempires;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.SwissSMPler;

public class PlayerCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(AgeOfEmpiresTauntsPlugin.getPrefix()+" Kann nur ingame verwendet werden.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if(args.length>0 && (args[0].toLowerCase().equals("mute") || args[0].toLowerCase().equals("stumm"))) {
			TauntSetting newSetting = PlayerSettings.get(player)==TauntSetting.MUTE ? TauntSetting.ALLOW : TauntSetting.MUTE;
			PlayerSettings.set(player, newSetting);
			
			String message;
			if(newSetting==TauntSetting.ALLOW) {
				message = ChatColor.GREEN+"Taunts aktiviert.";
			}
			else {
				message = ChatColor.RED+"Taunts stummgeschaltet.";
			}
			
			SwissSMPler.get(player).sendActionBar(message);
			return true;
		}
		
		TauntsView.open(player);
		return true;
	}

}
