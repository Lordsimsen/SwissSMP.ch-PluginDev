package ch.swisssmp.utils;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

public class EventPointCommand implements TabExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		switch(args[0]){
		case "take":
		case "give":{
			if(args.length<5) return false;
			if(!StringUtils.isNumeric(args[2])) return false;
			String playerName = args[1];
			int amount = Math.abs(Integer.parseInt(args[2]));
			if(args[0].equals("take")) amount*=-1;
			String currencyType = args[3];
			List<String> reasonParts = new ArrayList<>(Arrays.asList(args).subList(4, args.length));
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

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if(args.length<=1){
			List<String> options = Arrays.asList("reload", "give", "take", "summon");
			String current = args.length>0 ? args[0] : "";
			return StringUtil.copyPartialMatches(current, options, new ArrayList<>());
		}

		switch(args[0]){
			case "give":
			case "take":
			{
				if(args.length<=2){
					List<String> options = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
					String current = args.length>1 ? args[1] : "";
					return StringUtil.copyPartialMatches(current, options, new ArrayList<>());
				}
				if(args.length==3){
					return Collections.singletonList("<Menge>");
				}
				if(args.length==4){
					List<String> options = CurrencyInfo.getAll().stream().map(CurrencyInfo::getCurrencyType).collect(Collectors.toList());
					String current = args[3];
					return StringUtil.copyPartialMatches(current, options, new ArrayList<>());
				}
				return Collections.singletonList("<Grund> (z.B. Teilnahme an Ritterspielen)");
			}
			case "summon":{
				List<String> options = CurrencyInfo.getAll().stream().map(CurrencyInfo::getCurrencyType).collect(Collectors.toList());
				String current = args.length>1 ? args[1] : "";
				return StringUtil.copyPartialMatches(current, options, new ArrayList<>());
			}
			default: return Collections.emptyList();
		}
	}
}
