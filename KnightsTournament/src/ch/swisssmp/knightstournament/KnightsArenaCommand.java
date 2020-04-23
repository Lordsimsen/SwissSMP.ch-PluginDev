package ch.swisssmp.knightstournament;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class KnightsArenaCommand implements CommandExecutor{
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(args==null || args.length==0) return false;
		switch(args[0]){
		case "create":{
			if(!(sender instanceof Player)){
				sender.sendMessage("Can only be used from within the game.");
				return true;
			}
			Player player = (Player) sender;
			KnightsArena arena = KnightsArena.create(player.getWorld());
			KnightsArena.save(player.getWorld());
			PlayerInventory inventory = player.getInventory();
			if(inventory.getItemInMainHand()==null) {
				inventory.setItemInMainHand(arena.getTokenStack());
			} else {
				inventory.addItem(arena.getTokenStack());
			}
			arena.openEditor(player);
			return true;		
		}
		default:
			return false;
		}
	}
}
