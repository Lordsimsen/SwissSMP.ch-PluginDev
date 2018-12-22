package ch.swisssmp.shops;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		switch(args[0]){
		case "create":{
			if(!(sender instanceof Player)) return true;
			Player player = (Player) sender;
			Shop.create(player, player.getLocation().add(0, -1, 0));
			sender.sendMessage("[§dMarktplatz§r] §EShop erstellt.");
			break;
		}
		case "info":{
			if(args.length<2) return false;
			if(!(sender instanceof Player)) return true;
			int shop_id = -1;
			try{
				shop_id = Integer.parseInt(args[1]);
			}
			catch(Exception e){
				return false;
			}
			Shop shop = Shop.get(shop_id);
			if(shop==null){
				sender.sendMessage("[ShopManager] Shop "+shop_id+" nicht gefunden.");
				return true;
			}
			sender.sendMessage("[ShopManager] Info zum Shop "+shop.getName());
			sender.sendMessage("Besitzer: "+shop.getOwnerName());
			sender.sendMessage("Beruf: "+shop.getProfession());
			break;
		}
		default: return false;
		}
		return true;
	}

}
