package ch.swisssmp.prismsearch;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return true;
		Player player = (Player) sender;
		Search search = Main.searches.get(player.getUniqueId());
		if(args==null) args = new String[0];
		if(search==null){
			search = new Search(player);
			player.sendMessage(ChatColor.DARK_AQUA+"Suche gestartet.");
			player.sendMessage(ChatColor.GRAY+"Mit '/search output' Ingame Output umschalten.");
		}
		else if(args.length<1){
			search.finish();
			player.sendMessage(ChatColor.DARK_AQUA+"Suche beendet.");
		}
		if(args.length>0){
			switch(args[0]){
				case "output":{
					search.output = !search.output;
					break;
				}
				case "page":{
					if(args.length<2){
						player.sendMessage(ChatColor.DARK_AQUA+"Zuletzt durchsuchte Seite: "+search.page+"/"+search.pages);
						break;
					}
					else{
						if(!StringUtils.isNumeric(args[1])){
							player.sendMessage("/search page [number]");
							return true;
						}
						int newPage = Integer.parseInt(args[1]);
						search.page = newPage;
						search.output();
					}
				}
			}
		}
		return true;
	}
}
