package ch.swisssmp.mapimageloader;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;

public final class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		switch(args[0]){
		case "reload":{
			if(args.length<2) return false;
			Player player = (Player) sender;
			String url = args[1];
			Optional<MapImage> existing = MapImages.getByUrl(url);
			if(!existing.isPresent()) {
				sender.sendMessage(MapImageLoaderPlugin.getPrefix()+" Bild nicht gefunden.");
				return true;
			}
			existing.get().reloadImage(true);
			player.sendMessage("[ImageLoader] Das Bild wird geladen.");
			return true;
		}
		case "replace":{
			if(args.length<3) return false;
			Player player = (Player) sender;
			String oldUrl = args[1];
			String url = args[2];
			Optional<MapImage> existing = MapImages.getByUrl(oldUrl);
			if(!existing.isPresent()) {
				sender.sendMessage(MapImageLoaderPlugin.getPrefix()+" Bild nicht gefunden.");
				return true;
			}
			existing.get().replace(url);
			MapImages.save();
			player.sendMessage("[ImageLoader] Das Bild wird ersetzt.");
			return true;
		}
		case "load":{
			if(!(sender instanceof Player)) return true;
			if(args.length<2) return false;
			Player player = (Player) sender;
			String url = args[1];
			Optional<MapImage> existing = MapImages.getByUrl(url);
			if(existing.isPresent()) {
				existing.get().reloadImage(true);
				player.sendMessage("[ImageLoader] Das Bild wird geladen.");
				return true;
			}
			boolean keepLocalCopy = args.length>2 ? args[2].equals("true") : true;
			PlayerInventory playerInventory = player.getInventory();
			ItemStack mainhand = playerInventory.getItemInMainHand();
			ItemStack offhand = playerInventory.getItemInOffHand();
			ItemStack itemStack;
			if(mainhand!=null && (mainhand.getType()==Material.MAP || mainhand.getType()==Material.FILLED_MAP)){
				itemStack = mainhand;
			}
			else if(offhand!=null && (offhand.getType()==Material.MAP || offhand.getType()==Material.FILLED_MAP)){
				itemStack = offhand;
			}
			else itemStack = null;
			ItemMeta itemMeta = itemStack!=null ? itemStack.getItemMeta() : null;
			MapMeta mapMeta = itemMeta!=null && itemMeta instanceof MapMeta ? (MapMeta) itemMeta : null;
			if(mapMeta==null) {
				player.sendMessage("[ImageLoader] Du hast keine Karte in der Hand.");
				return true;
			}
			MapImage image = MapImage.create(url, keepLocalCopy);
			MapViewComposition composition = MapViewComposition.create(mapMeta.getMapView());
			composition.addImage(image);
			MapImages.save();
			MapViewCompositions.save();
			player.sendMessage("[ImageLoader] Das Bild wird geladen.");
			return true;
		}
		case "remove":{
			if(args.length<3) return false;
			String key = args[2];
			switch(args[1]) {
			case "composition":{
				int mapId;
				try {
					mapId = Integer.parseInt(key);
				}
				catch(Exception e) {
					return false;
				}
				Optional<MapViewComposition> c = MapViewComposition.get(mapId);
				if(!c.isPresent()) {
					sender.sendMessage(MapImageLoaderPlugin.getPrefix()+" Komposition nicht gefunden.");
					return true;
				}
				c.get().remove();
				MapViewCompositions.save();
				sender.sendMessage(MapImageLoaderPlugin.getPrefix()+" Komposition gelöscht.");
				return true;
			}
			case "image":{
				UUID imageUid;
				try {
					imageUid = UUID.fromString(key);
				}
				catch(Exception e) {
					return false;
				}
				Optional<MapImage> i = MapImage.get(imageUid);
				if(!i.isPresent()) {
					sender.sendMessage(MapImageLoaderPlugin.getPrefix()+" Bild nicht gefunden.");
					return true;
				}
				i.get().remove();
				MapImages.save();
				sender.sendMessage(MapImageLoaderPlugin.getPrefix()+" Bild gelöscht.");
				return true;
			}
			}
		}
		default:return false;
		}
	}

}
