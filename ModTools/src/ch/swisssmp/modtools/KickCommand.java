package ch.swisssmp.modtools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class KickCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission(command.getPermission())) {
			return true;
		}
		if(args==null || args.length<1) return false;
		String reason = args.length > 1 ? args[1] : "";
		Player player = Bukkit.getPlayer(args[0]);
		if(player==null){
			sender.sendMessage(ModTools.getPrefix()+ChatColor.GRAY+args[0]+" nicht gefunden.");
			return true;
		}
		player.kickPlayer(!reason.isEmpty() ? reason : "Du wurdest gekickt!");
		sender.sendMessage(ModTools.getPrefix()+player.getName()+ChatColor.RED+" gekickt!");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if(command==null 
				|| !command.getName().equals("kick") 
				|| !sender.hasPermission(command.getPermission())
				) {
			return new ArrayList<String>();
		}
		if(args!=null && args.length>=2) {
			if(args.length>2) return new ArrayList<String>();
			return Arrays.asList("Beleidigungen","Spam","(Anderes, ausformulieren)");
		}
		String query = args!=null && args.length>0 ? args[0].toLowerCase() : "";
		return Bukkit.getOnlinePlayers().stream().map(p->p.getName()).filter(n->n.toLowerCase().startsWith(query)).sorted().collect(Collectors.toList());
	}
}
