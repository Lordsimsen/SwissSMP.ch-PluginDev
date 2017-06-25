package ch.swisssmp.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.webcore.DataSource;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		switch(label){
		case "balance":{
			if(!(sender instanceof Player)) return true;
			Player player = (Player) sender;
			try {
				YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("players/balance.php", new String[]{"player="+URLEncoder.encode(player.getUniqueId().toString(), "utf-8")});
				if(yamlConfiguration==null) return true;
				if(!yamlConfiguration.contains("message")) return true;
				for(String line : yamlConfiguration.getStringList("message"))
					sender.sendMessage(line);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			break;
		}
		case "seen":{
			if(args==null || args.length<1) return false;
			try {
				YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("players/seen.php", new String[]{"player="+URLEncoder.encode(args[0], "utf-8")});
				if(yamlConfiguration==null) return true;
				if(!yamlConfiguration.contains("message")) return true;
				for(String line : yamlConfiguration.getStringList("message"))
					sender.sendMessage(line);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			break;
		}
		case "afk":{
			if(!(sender instanceof Player)){
				return true;
			}
			Player player = (Player) sender;
			boolean afk = SwissSMPler.afk_tasks.containsKey(player.getUniqueId());
			SwissSMPler.last_vectors.put(player.getUniqueId(), player.getLocation().toVector());
			SwissSMPler.get(player).setAfk(!afk);
			break;
		}
		case "list":{
			try {
				List<String> arguments = new ArrayList<String>();
				for(Player player : Bukkit.getOnlinePlayers()){
					arguments.add("players[]="+URLEncoder.encode(player.getName(), "utf-8"));
				}
				String[] argumentsArray = new String[arguments.size()];
				YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("players/list.php", arguments.toArray(argumentsArray));
				if(yamlConfiguration==null) return true;
				if(!yamlConfiguration.contains("message")) return true;
				for(String line : yamlConfiguration.getStringList("message"))
					sender.sendMessage(line);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			break;
		}
		case "tell":{
			if(args==null || args.length<2) return false;
			String senderName = ChatColor.RED+"Konsole";
			if(sender instanceof Player){
				senderName = ((Player)sender).getDisplayName();
			}
			Player recipient = Bukkit.getPlayer(args[0]);
			String[] textParts = Arrays.copyOfRange(args, 1, args.length);
			String text = String.join(" ", Arrays.asList(textParts));
			recipient.sendMessage(ChatColor.DARK_GRAY+"["+ChatColor.RESET+senderName+ChatColor.RESET+ChatColor.DARK_GRAY+" >> "+ChatColor.GRAY+"ich"+ChatColor.DARK_GRAY+"] "+ChatColor.GOLD+text);
			sender.sendMessage(ChatColor.DARK_GRAY+"["+ChatColor.GRAY+"ich"+ChatColor.DARK_GRAY+" >> "+ChatColor.RESET+recipient.getDisplayName()+ChatColor.RESET+ChatColor.DARK_GRAY+"] "+ChatColor.GOLD+text);
			break;
		}
		case "worlds":{
			List<String> worldNames = new ArrayList<String>();
			for(World world : Bukkit.getWorlds()){
				worldNames.add(world.getName());
			}
			sender.sendMessage(String.join(", ", worldNames));
		}
		}
		return true;
	}

}
