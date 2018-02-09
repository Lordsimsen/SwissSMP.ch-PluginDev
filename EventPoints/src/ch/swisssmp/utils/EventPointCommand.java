package ch.swisssmp.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.webcore.DataSource;

public class EventPointCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		switch(args[0]){
		case "give":{
			if(args.length<3) return false;
			if(!StringUtils.isNumeric(args[2])) return false;
			String playerName = args[1];
			int amount = Math.abs(Integer.parseInt(args[2]));
			try {
				sender.sendMessage(DataSource.getResponse("players/event_points.php", new String[]{
						"player="+URLEncoder.encode(playerName, "utf-8"),
						"amount="+amount
				}));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		case "take":{
			if(args.length<3) return false;
			if(!StringUtils.isNumeric(args[2])) return false;
			String playerName = args[1];
			int amount = Math.abs(Integer.parseInt(args[2]));
			try {
				sender.sendMessage(DataSource.getResponse("players/event_points.php", new String[]{
						"player="+URLEncoder.encode(playerName, "utf-8"),
						"amount="+-amount
				}));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		case "summon":{
			if(!(sender instanceof Player)) return true;
			Player player = (Player) sender;
			player.getWorld().dropItem(player.getEyeLocation(), EventPoints.getItem(64));
			break;
		}
		default: return false;
		}
		return true;
	}

}
