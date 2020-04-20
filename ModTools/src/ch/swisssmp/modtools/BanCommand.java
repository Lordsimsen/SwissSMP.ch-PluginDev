package ch.swisssmp.modtools;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.BanList.Type;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class BanCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission(command.getPermission())) {
			return true;
		}
		if(args==null || args.length<1) return false;
		String reason = args.length > 1 ? args[1] : "";
		if(label.equals("ban")){
			Player player = Bukkit.getPlayer(args[0]);
			if(player==null){
				sender.sendMessage(ModTools.getPrefix()+ChatColor.GRAY+args[0]+" nicht gefunden.");
				return true;
			}
			Bukkit.getBanList(Type.NAME).addBan(player.getName(), reason, null, sender.getName());
			player.kickPlayer(!reason.isEmpty() ? reason : "Du wurdest gebannt!");
			sender.sendMessage(ModTools.getPrefix()+player.getName()+ChatColor.RED+" gebannt!");
			return true;
		}
		String ip = args[0];
		Bukkit.getBanList(Type.IP).addBan(ip, reason, null, sender.getName());
		sender.sendMessage(ModTools.getPrefix()+ChatColor.RED+args[0]+" gebannt!");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if(command==null 
				|| !command.getName().equals("ban") 
				|| !sender.hasPermission(command.getPermission())
				) {
			return new ArrayList<String>();
		}
		if(args!=null && args.length>=2) {
			if(args.length>2) return new ArrayList<String>();
			return Arrays.asList("Griefing","Cheating","Beleidigungen","Spam","(Anderes, ausformulieren)");
		}
		String query = args!=null && args.length>0 ? args[0].toLowerCase() : "";
		return Bukkit.getOnlinePlayers().stream().map(p->p.getName()).filter(n->n.toLowerCase().startsWith(query)).sorted().collect(Collectors.toList());
	}
}
