package ch.swisssmp.zones;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zones.zoneinfos.ZoneInfo;

public class ZoneEditorCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return true;
		if(args==null || args.length==0) return false;
		switch(args[0]){
		case "item":{
			if(args.length<2) return false;
			Player player = (Player) sender;
			ZoneInfo zoneInfo = ZoneInfo.get(player.getWorld(), args[1].toLowerCase());
			ItemStack itemStack = new ItemStack(Material.DIAMOND_PICKAXE);
			zoneInfo.apply(itemStack);
			player.getInventory().addItem(itemStack);
			return true;
		}
		case "start":{
			if(args.length<2) return false;
			Player player = (Player) sender;
			String regionId = args[1];
			ZoneEditor current = ZoneEditor.get(player);
			if(current!=null){
				current.complete();
				return true;
			}
			
			ZoneInfo zoneInfo = ZoneInfo.get(player.getWorld(), regionId);
			if(zoneInfo==null){
				SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Etwas ist schiefgelaufen.");
				return true;
			}
			ZoneEditor.start(player, zoneInfo.createItemStack(), zoneInfo);
			SwissSMPler.get(player).sendActionBar(ChatColor.AQUA+"Editor gestartet!");
			return true;
		}
		case "end":{
			Player player = (Player) sender;
			ZoneEditor current = ZoneEditor.get(player);
			if(current!=null){
				current.complete();
				SwissSMPler.get(player).sendActionBar(ChatColor.GREEN+"Region gespeichert!");
				return true;
			}
			return true;
		}
		case "cancel":{
			Player player = (Player) sender;
			ZoneEditor current = ZoneEditor.get(player);
			if(current!=null){
				current.cancel();
				SwissSMPler.get(player).sendActionBar(ChatColor.GRAY+"Editor abgebrochen.");
				return true;
			}
			return true;
		}
		default:
			return false;
		}
	}
}
