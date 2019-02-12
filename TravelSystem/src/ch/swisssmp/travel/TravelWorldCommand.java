package ch.swisssmp.travel;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TravelWorldCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
if(args==null || args.length==0) return false;
		
		switch(args[0]){
		case "bearbeite":
		case "edit":{
			if(!(sender instanceof Player)){
				sender.sendMessage("Can only be used from within the game.");
				return true;
			}
			Player player = (Player) sender;

			String name;
			if(args.length>1){
				name = args[1];
			}
			else{
				ItemStack itemStack = player.getInventory().getItemInMainHand();
				TravelStation station = TravelStation.get(itemStack);
				if(station==null){
					return false;
				}
				name = station.getTravelWorldName();
				if(name==null){
					player.sendMessage(TravelSystem.getPrefix()+ChatColor.RED+"Diese Station hat noch keine Reisewelt zugewiesen.");
					return true;
				}
			}
			TravelWorld.edit(name, player);			
			return true;
		}
		case "endedit":{
			if(!(sender instanceof Player)){
				sender.sendMessage("Can only be used from within the game.");
				return true;
			}
			Player player = (Player) sender;
			World world = player.getWorld();
			TravelWorld.endedit(world, sender);
			return true;
		}
		default: return true;
		}
	}
}
