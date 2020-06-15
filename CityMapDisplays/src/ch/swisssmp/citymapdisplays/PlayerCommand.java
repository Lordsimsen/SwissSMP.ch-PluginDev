package ch.swisssmp.citymapdisplays;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.city.City;
import ch.swisssmp.utils.SwissSMPler;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args!=null && args.length>0){
			switch(args[0].toLowerCase()){
			case "reload":{
				if(!sender.hasPermission("citymapdisplays.admin")){
					return true;
				}
				CityMapDisplays.load();
				sender.sendMessage(CityMapDisplaysPlugin.getPrefix()+ChatColor.GREEN+" Anzeigen neu geladen.");
				return true;
			}
			case "create":{
				if(!sender.hasPermission("citymapdisplays.admin")){
					return true;
				}
				if(args.length<2) return false;
				int width;
				int height;
				try {
					width = Integer.parseInt(args[1]);
					height = args.length>2 ? Integer.parseInt(args[2]) : width;
				}
				catch(Exception e) {
					return false;
				}
				String name;
				if(args.length>3){
					String[] nameParts = new String[args.length-3];
					for(int i = 3; i < args.length; i++){
						nameParts[i-3] = args[i];
					}
					name = String.join(" ", nameParts);
				}
				else{
					name = null;
				}
				CityMapDisplay display = CityMapDisplay.create(name, width, height);
				if(sender instanceof Player) {
					ItemStack itemStack = display.getItemStack();
					Player player = (Player) sender;
					player.getWorld().dropItem(player.getEyeLocation(), itemStack);
				}
				CityMapDisplays.save();
				sender.sendMessage(CityMapDisplaysPlugin.getPrefix()+ChatColor.GREEN+" Anzeige erstellt.");
				return true;
			}
			case "remove":{
				if(!sender.hasPermission("citymapdisplays.admin")){
					return true;
				}
				if(args.length<2) return false;
				String key = args[1];
				UUID displayUid;
				try {
					displayUid = UUID.fromString(key);
				}
				catch(Exception e) {
					e.printStackTrace();
					sender.sendMessage(CityMapDisplaysPlugin.getPrefix()+" Anzeige nicht gefunden.");
					return true;
				}
				Optional<CityMapDisplay> displayQuery = CityMapDisplay.get(displayUid);
				if(!displayQuery.isPresent()){
					sender.sendMessage(CityMapDisplaysPlugin.getPrefix()+" Anzeige nicht gefunden.");
					return true;
				}

				displayQuery.get().remove();
				sender.sendMessage(CityMapDisplaysPlugin.getPrefix()+" Anzeige "+displayQuery.get().getName()+" entfernt.");
				return true;
			}
			case "show":{
				if(!sender.hasPermission("citymapdisplays.viewer")){
					return true;
				}
				if(args.length<3) return false;
				String key = args[1];
				String cityIdString = args[2];
				UUID displayUid;
				int cityId;
				try {
					displayUid = UUID.fromString(key);
					cityId = Integer.parseInt(cityIdString);
				}
				catch(Exception e) {
					e.printStackTrace();
					sender.sendMessage(CityMapDisplaysPlugin.getPrefix()+" Bei der Auswahl der Stadt ist ein Fehler aufgetreten.");
					return true;
				}
				Optional<CityMapDisplay> displayQuery = CityMapDisplay.get(displayUid);
				City city = City.get(cityId);
				if(!displayQuery.isPresent() || city==null) {
					Bukkit.getLogger().info(CityMapDisplaysPlugin.getPrefix()+"Could not display city map: is display present? ("+displayQuery.isPresent()+", is city present? ("+city+")");
					sender.sendMessage(CityMapDisplaysPlugin.getPrefix()+" Bei der Auswahl der Stadt ist ein Fehler aufgetreten.");
					return true;
				}
				CityMapDisplay display = displayQuery.get();
				display.applyCity(city);
				if(sender instanceof Player) {
					Player player = (Player) sender;
					SwissSMPler.get((Player)sender).sendActionBar("Du betrachtest jetzt "+ChatColor.AQUA+city.getName()+ChatColor.RESET+".");
					player.getWorld().playSound(player.getEyeLocation(), Sound.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS, 1, 1);
				}
				else {
					sender.sendMessage(CityMapDisplaysPlugin.getPrefix()+" Zeige Stadt "+city.getName());
				}
				return true;
			}
			default:
				return false;
			}
		}
		return false;
	}

}