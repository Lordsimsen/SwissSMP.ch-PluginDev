package ch.swisssmp.chatmanager;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.webcore.DataSource;
import net.md_5.bungee.api.ChatColor;

public class PlayerChat implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null) return false;
		if(args.length<2) return false;
		String name = args[0];
		Player player = Bukkit.getPlayer(name);
		List<String> messageParts = new LinkedList<String>(Arrays.asList(args));
		messageParts.remove(0);
		String rawMessage = String.join(" ", messageParts);
		String message;
		if(player==null){
			messageParts.add(0, ChatColor.GRAY+"[Konsole/"+ChatColor.DARK_GREEN+name+ChatColor.RESET+ChatColor.GRAY+"]");
			message = String.join(" ", messageParts);
			for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
				onlinePlayer.sendMessage(message);
			}
			try{
				String player_uuid = name;
				String world = "web";
				args[0] = "";
				DataSource.getResponse("chat_notifications/chat.php", new String[]{
					"player_uuid="+player_uuid,
					"name="+name,
					"world="+world,
					"message="+URLEncoder.encode(rawMessage, "utf-8")
				});
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		else{
			message = String.join(" ", messageParts);
			player.chat(message);
		}
		return true;
	}

}
