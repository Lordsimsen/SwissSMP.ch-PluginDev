package ch.swisssmp.shops;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		switch(args[0]){
		case "reload":{
			if(args.length<2){
				ShopManager.plugin.loadWorlds();
				sender.sendMessage("[ShopManager] Shops und Marktplätze neu geladen.");
			}
			else if(args[1].toLowerCase().equals("shops")){
				for(ShoppingWorld shoppingWorld : ShoppingWorld.getWorlds()){
					shoppingWorld.reloadShops();
				}
				sender.sendMessage("[ShopManager] Shops neu geladen.");
			}
			else if(args[1].toLowerCase().equals("marketplaces")){
				for(ShoppingWorld shoppingWorld : ShoppingWorld.getWorlds()){
					shoppingWorld.reloadMarketplaces();
				}
				sender.sendMessage("[ShopManager] Marktplätze neu geladen.");
			}
			else if(args[1].toLowerCase().equals("agents")){
				for(ShoppingWorld shoppingWorld : ShoppingWorld.getWorlds()){
					for(Marketplace marketplace : shoppingWorld.marketplaces.values()){
						marketplace.updateRepresentedShops();
					}
				}
				sender.sendMessage("[ShopManager] Agenten aktualisiert.");
			}
			break;
		}
		case "create":{
			if(!(sender instanceof Player)) return true;
			Player player = (Player) sender;
			Shop.create(player, -1, null, player.getLocation().add(0, -1, 0));
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
			Player player = (Player) sender;
			ShoppingWorld shoppingWorld = ShoppingWorld.get(player.getWorld());
			Shop shop = shoppingWorld.getShop(shop_id);
			if(shop==null){
				sender.sendMessage("[ShopManager] Shop "+shop_id+" nicht gefunden.");
				return true;
			}
			sender.sendMessage("[ShopManager] Info zum Shop "+shop.getName());
			sender.sendMessage("Besitzer: "+shop.getOwnerName());
			Marketplace marketplace = shop.getMarketplace();
			if(marketplace!=null){
				sender.sendMessage("Hauptstandort: "+marketplace.getName());
			}
			sender.sendMessage("Beruf: "+shop.getProfession());
			sender.sendMessage("Anzahl Vertreter: "+shop.getAgents().length);
			break;
		}
		case "marketplaces":{
			if(!(sender instanceof Player)) return true;
			World world = ((Player)sender).getWorld();
			ShoppingWorld shoppingWorld = ShoppingWorld.get(world);
			if(shoppingWorld==null) return true;
			Marketplace[] marketplaces = shoppingWorld.getMarketplaces();
			sender.sendMessage("[ShopManager] "+marketplaces.length+" Marktplätze:");
			for(Marketplace marketplace : marketplaces){
				sender.sendMessage(marketplace.getAddonInstanceId()+": "+marketplace.getName());
			}
			break;
		}
		default: return false;
		}
		return true;
	}

}
