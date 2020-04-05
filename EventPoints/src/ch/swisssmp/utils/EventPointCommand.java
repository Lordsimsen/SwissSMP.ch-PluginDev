package ch.swisssmp.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EventPointCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		switch(args[0]){
		case "give":{
			if(args.length<5) return false;
			if(!StringUtils.isNumeric(args[2])) return false;
			String playerName = args[1];
			int amount = Math.abs(Integer.parseInt(args[2]));
			String currencyType = args[3];
			List<String> reasonParts = new ArrayList<String>();
			for(int i = 4; i < args.length; i++){
				reasonParts.add(args[i]);
			}
			String reason = String.join(" ", reasonParts);
			EventPoints.give(sender, playerName, amount, currencyType, reason);
			break;
		}
		case "take":{
			if(args.length<5) return false;
			if(!StringUtils.isNumeric(args[2])) return false;
			String playerName = args[1];
			int amount = Math.abs(Integer.parseInt(args[2]));
			String currencyType = args[3];
			List<String> reasonParts = new ArrayList<String>();
			for(int i = 4; i < args.length; i++){
				reasonParts.add(args[i]);
			}
			String reason = String.join(" ", reasonParts);
			EventPoints.give(sender, playerName, amount, currencyType, reason);
			break;
		}
		case "summon":{
			if(!(sender instanceof Player)) return true;
			String currencyType = args.length>1 ? args[1] : "EVENT_POINT";
			Player player = (Player) sender;
			ItemStack itemStack = EventPoints.getItem(64, currencyType);
			if(itemStack==null || itemStack.getType()==Material.AIR || itemStack.getAmount()==0){
				sender.sendMessage(EventPoints.getPrefix()+"Konnte den ItemStack nicht generieren.");
				return true;
			}
			player.getWorld().dropItem(player.getEyeLocation(), itemStack);
			break;
		}
		case "reload":{
			CurrencyInfo.clear();
			break;
		}
		default: return false;
		}
		return true;
	}

}
