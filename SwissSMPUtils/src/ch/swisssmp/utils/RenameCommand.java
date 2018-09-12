package ch.swisssmp.utils;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class RenameCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("[SwissSMPUtils] /rename kann nur ingame verwendet werden.");
			return true;
		}
		if(args.length==0) return false;
		Player player = (Player)sender;
		if(player.getGameMode()!=GameMode.CREATIVE){
			sender.sendMessage("[SwissSMPUtils] /rename kann nur im Kreativ Modus verwendet werden.");
			return true;
		}
		String name = StringUtils.join(args, " ").replaceAll("\\$", "§");
		PlayerInventory playerInventory = player.getInventory();
		if(playerInventory.getItemInMainHand()==null && playerInventory.getItemInOffHand()==null) return true;
		ItemStack itemStack = playerInventory.getItemInMainHand()!=null  ? playerInventory.getItemInMainHand() : playerInventory.getItemInOffHand();
		PlayerRenameItemEvent event = new PlayerRenameItemEvent(player, itemStack, name);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()){
			sender.sendMessage("[CustomItems] Konnte das Item nicht umbenennen.");
			return true;
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(event.getNewName());
		itemStack.setItemMeta(itemMeta);
		SwissSMPler.get((Player)sender).sendActionBar("Item umbenennt.");
		return true;
	}

}
