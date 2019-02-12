package ch.swisssmp.utils;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class WaypointCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("Can only be used from within the game.");
			return true;
		}
		Player player = (Player) sender;
		ItemStack itemStack = WaypointAPI.getItem(new Position(player.getLocation()), args.length>0 ? MarkerType.valueOf(args[0]) : MarkerType.RED);
		World world = player.getWorld();
		WaypointAPI.setAttachedWorld(itemStack, world);
		
		PlayerInventory inventory = player.getInventory();
		if(inventory.getItemInMainHand()==null){
			inventory.setItemInMainHand(itemStack);
		}
		else{
			inventory.addItem(itemStack);
		}
		
		SwissSMPler.get(player).sendActionBar(ChatColor.GREEN+"Wegpunkt erstellt!");
		
		return true;
	}
}
