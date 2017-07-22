package ch.swisssmp.flyday;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ch.swisssmp.webcore.DataSource;

public class FlyDayCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0){
			return false;
		}
		switch(args[0].toLowerCase()){
		case "reload":{
			FlyDay.updateState();
			sender.sendMessage("[FlyDay] FlyDay Status aktualisiert.");
			break;
		}
		case "on":{
			if(args.length<2){
				return false;
			}
			try{
				String[] worlds = args[1].split(",");
				ArrayList<String> arguments = new ArrayList<String>();
				for(String world : worlds){
					if(Bukkit.getWorld(world)==null){
						sender.sendMessage("[§7FlyDay§r] Welt "+world+" nicht gefunden.");
						continue;
					}
					arguments.add("worlds[]="+URLEncoder.encode(world, "utf-8"));
				}
				arguments.add("global_flight=1");
				DataSource.getResponse("flyday/set.php", arguments.toArray(new String[arguments.size()]));
			}
			catch(Exception e){
				e.printStackTrace();
			}
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast §aHeute ist §EFlyDay§a! Wir wünschen euch viel Vergnügen beim Bauen und Erkunden.");
			FlyDay.updateState();
			sender.sendMessage("[FlyDay] FlyDay gestartet.");
			break;
		}
		case "off":{
			DataSource.getResponse("flyday/set.php", new String[]{
					"global_flight=0"
			});
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast §aDer §EFlyDay§a ist nun vorbei.");
			FlyDay.updateState();
			sender.sendMessage("[FlyDay] FlyDay beendet.");
			break;
		}
		default:
			return false;
		}
		return true;
	}

}
