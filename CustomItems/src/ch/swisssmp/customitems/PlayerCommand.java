package ch.swisssmp.customitems;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		switch(args[0]){
		case "summon":{
			if(args.length<2) return false;
			if(!(sender instanceof Player)){
				sender.sendMessage("[CustomItems] Kann nur ingame verwendet werden.");
				return true;
			}
			CustomItemBuilder customItemBuilder;
			Player player = (Player) sender;
			if(StringUtils.isNumeric(args[1])){
				if(args.length>2 && StringUtils.isNumeric(args[2])){
					customItemBuilder = CustomItems.getCustomItemBuilder(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
				}
				else{
					customItemBuilder = CustomItems.getCustomItemBuilder(Integer.parseInt(args[1]));
				}
			}
			else{
				if(args.length>2 && StringUtils.isNumeric(args[2])){
					customItemBuilder = CustomItems.getCustomItemBuilder(args[1], Integer.parseInt(args[2]));
				}
				else{
					customItemBuilder = CustomItems.getCustomItemBuilder(args[1]);
				}
			}
			if(customItemBuilder==null){
				sender.sendMessage("[CustomItems] Konnte den ItemBuilder nicht generieren.");
				return true;
			}
			ItemStack itemStack = customItemBuilder.build();
			if(itemStack==null){
				sender.sendMessage("[CustomItems] Konnte den ItemStack nicht generieren.");
				return true;
			}
			sender.sendMessage("[CustomItems] "+itemStack.getAmount()+"x "+itemStack.getItemMeta().getDisplayName()+"§r generiert!");
			player.getWorld().dropItem(player.getEyeLocation(), itemStack);
			break;
		}
		case "inspect":{
			sender.sendMessage("Noch nicht implementiert.");
			break;
		}
		default:
			return false;
		}
		return true;
	}

}
