package ch.swisssmp.city;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.PlayerInfo;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public class CitiesCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args!=null && args.length>0){
			switch(args[0].toLowerCase()){
			case "reload":{
				Cities.load();
				sender.sendMessage(CitySystemPlugin.getPrefix()+ChatColor.GREEN+"Städte neu geladen.");
				return true;
			}
			case "delete":{
				if(args.length<2) return false;
				String key = args[1];
				if(!StringUtils.isNumeric(key) && key.length()<2){
					sender.sendMessage(CitySystemPlugin.getPrefix()+"Name muss mindestens zwei Zeichen lang sein.");
					return true;
				}
				HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), "delete_city.php", new String[]{
						"city="+URLEncoder.encode(key),
						"world="+URLEncoder.encode(Bukkit.getWorlds().get(0).getName())
				});
				request.onFinish(()->{
					sender.sendMessage(CitySystemPlugin.getPrefix()+request.getResponse());
					Cities.remove(key);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "permission reload");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "addon reload");
				});
				return true;
			}
			case "ring":{
				if(!(sender instanceof Player)){
					sender.sendMessage("[Städtesystem] Befehl kann nur ngame verwendet werden.");
					return true;
				}
				if(args.length<3) return false;
				Player player = (Player) sender;
				String key = args[1];
				City city = City.get(key);
				if(city==null){
					sender.sendMessage(CitySystemPlugin.getPrefix()+ChatColor.RED+"Stadt "+key+" nicht gefunden.");
					return true;
				}
				
				PlayerInfo owner;
				String ring_type;
				if(args.length>3){
					ring_type = args[3];
					CitizenInfo citizen = city.getCitizen(args[2]);
					if(citizen==null){
						sender.sendMessage(CitySystemPlugin.getPrefix()+ChatColor.RED+"Bürger "+args[2]+" nicht gefunden.");
						return true;
					}
					owner = citizen.getPlayerInfo();
				}
				else{
					ring_type = args[2];
					owner = PlayerInfo.get(player);
				}
				
				ItemStack itemStack = ItemManager.createRing(ring_type, city, owner);
				player.getWorld().dropItem(player.getEyeLocation(), itemStack);
				return true;
			}
			default:
				return false;
			}
		}
		if(!(sender instanceof Player)){
			sender.sendMessage(CitySystemPlugin.getPrefix()+"Kann nur ingame verwendet werden.");
			return true;
		}
		
		Player player = (Player) sender;
		CitiesView.open(player);
		return true;
	}

}
