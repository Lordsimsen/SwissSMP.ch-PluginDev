package ch.swisssmp.taxcollector;

import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ch.swisssmp.webcore.DataSource;

public class ConsoleCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null) return false;
		if(args.length<1) return false;
		switch(args[0]){
		case "collect":{
			if(args.length<2){
				TaxCollector.collect();
			}
			else if(StringUtils.isNumeric(args[1])){
				TaxCollector.collect(Integer.parseInt(args[1]));
			}
			else return false;
			sender.sendMessage("[TaxCollector] Opfergaben erfolgreich eingesammelt.");
			break;
		}
		case "reload":{
			TaxCollector.reloadChests();
			sender.sendMessage("[TaxCollector] Opfergabenkisten erfolgreich aktualisiert.");
			break;
		}
		case "info":{
			if(args.length<2){
				sender.sendMessage("/tax info [player]");
				return true;
			}
			TaxCollector.inform_player(new String[]{
					"player="+args[1],
					"flags[]=other"
			}, sender);
			break;
		}
		case "chests":{
			for(Entry<Integer,TaxChest>taxChest:TaxCollector.taxChests.entrySet()){
				TaxChest chest = taxChest.getValue();
				sender.sendMessage("city_"+taxChest.getKey()+": "+chest.getWorldName()+"-"+chest.getX()+","+chest.getY()+","+chest.getZ());
			}
			break;
		}
		case "schedule":{
			DataSource.getResponse("taxes/schedule.php");
			break;
		}
		default: 
			return false;
		}
		return true;
	}

}
