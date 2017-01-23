package ch.swisssmp.chatnotifier;

import java.net.URLEncoder;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.webcore.DataSource;
import net.md_5.bungee.api.ChatColor;

public class PlayerChat implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] args) {
		if(args==null) return false;
		if(args.length<2) return false;
		Player sender = Bukkit.getPlayer(args[0]);
		if(sender==null){
			args[0] = ChatColor.DARK_GREEN+"["+args[0]+"] (Web):"+ChatColor.RESET;
		}
		else{
			args[0] = ChatColor.RESET+"["+sender.getDisplayName()+ChatColor.RESET+"]";
		}
		String message = String.join(" ", Arrays.asList(args));
		for(Player player : Bukkit.getOnlinePlayers()){
			player.sendMessage(message);
		}
		try{
			String[] parts = new String[args.length-1];
			String player_uuid;
			String name;
			String world;
			if(sender!=null){
				player_uuid = sender.getUniqueId().toString();
				name = sender.getName();
				world = sender.getWorld().getName();
			}
			else{
				player_uuid = args[0];
				name = args[0];
				world = "web";
			}
			for(int i = 1; i < args.length; i++){
				parts[i-1] = args[i];
			}
			String trimmedMessage = String.join(" ", Arrays.asList(parts));
			DataSource.getResponse("chat_notifications/chat.php", new String[]{
				"player_uuid="+player_uuid,
				"name="+name,
				"world="+world,
				"message="+URLEncoder.encode(trimmedMessage, "utf-8")
			});
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}

}
