package ch.swisssmp.modtools;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.BanEntry;
import org.bukkit.BanList.Type;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class PardonCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission(command.getPermission())) {
			return true;
		}
		if(args==null || args.length<1) return false;
		Type banListType = label.equals("pardon") ? Type.NAME : Type.IP;
		BanEntry entry = Bukkit.getBanList(banListType).getBanEntry(args[0]);
		if(entry==null){
			sender.sendMessage(ModTools.getPrefix()+ChatColor.GRAY+"Kein Eintrag zu "+args[0]+" gefunden.");
			return true;
		}
		Bukkit.getBanList(banListType).pardon(entry.getTarget());
		sender.sendMessage(ModTools.getPrefix()+ChatColor.GREEN+entry.getTarget()+" entbannt!");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if(command==null 
				|| !command.getName().equals("pardon") 
				|| !sender.hasPermission(command.getPermission())
				) {
			return new ArrayList<String>();
		}
		String query = args!=null && args.length>0 ? args[0] : "";
		if(label.equals("pardon")){
			return Bukkit.getBannedPlayers().stream().map(p->p.getName()).filter(n->n.startsWith(query)).sorted().collect(Collectors.toList());
		}
		else{
			return Bukkit.getBanList(Type.IP).getBanEntries().stream().map(e->e.getTarget()).filter(t->t.startsWith(query)).sorted().collect(Collectors.toList());
		}
	}

}
