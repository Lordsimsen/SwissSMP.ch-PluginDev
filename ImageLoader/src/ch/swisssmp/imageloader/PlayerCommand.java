package ch.swisssmp.imageloader;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		switch(args[0]){
		case "load":{
			if(!(sender instanceof Player)) return true;
			if(args.length<2) return false;
			Player player = (Player) sender;
			String url = args[1];
			PlayerInventory playerInventory = player.getInventory();
			ItemStack mainhand = playerInventory.getItemInMainHand();
			ItemStack offhand = playerInventory.getItemInOffHand();
			if(mainhand!=null && (mainhand.getType()==Material.MAP || mainhand.getType()==Material.FILLED_MAP)){
				ImageLoaderPlugin.load(url, mainhand);
				player.sendMessage("[ImageLoader] Das Bild wird geladen.");
			}
			else if(offhand!=null && (offhand.getType()==Material.MAP || offhand.getType()==Material.FILLED_MAP)){
				ImageLoaderPlugin.load(url, offhand);
				player.sendMessage("[ImageLoader] Das Bild wird geladen.");
			}
			else{
				player.sendMessage("[ImageLoader] Du hast keine Karte in der Hand.");
				return true;
			}
			return true;
		}
		default:return false;
		}
	}

}
