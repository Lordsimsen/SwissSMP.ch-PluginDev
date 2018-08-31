package ch.swisssmp.customitems;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.utils.SwissSMPler;

public class RenameCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("[CustomItems] /rename kann nur ingame verwendet werden.");
			return true;
		}
		if(args.length==0) return false;
		String name = StringUtils.join(args, " ").replaceAll("$", "§");
		PlayerInventory playerInventory = ((Player)sender).getInventory();
		if(playerInventory.getItemInMainHand()==null && playerInventory.getItemInOffHand()==null) return true;
		ItemStack itemStack = playerInventory.getItemInMainHand()!=null  ? playerInventory.getItemInMainHand() : playerInventory.getItemInOffHand();
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(name);
		itemStack.setItemMeta(itemMeta);
		SwissSMPler.get((Player)sender).sendActionBar("Item umbenennt.");
		return true;
	}

}
