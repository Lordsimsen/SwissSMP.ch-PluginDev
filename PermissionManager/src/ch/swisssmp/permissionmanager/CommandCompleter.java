package ch.swisssmp.permissionmanager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class CommandCompleter implements TabCompleter{

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<String>();
		String type;
		String part;
		switch(label){
		case "pex":
		case "perm":
		case "permission":{
			if(args==null || args.length<2){
				result.add("user");
				result.add("rank");
				result.add("city");
				return result;
			}
			if(args.length==2){
				if(!args[0].equals("user") && !args[0].equals("rank") && !args[0].equals("city")){
					return result;
				}
				type = args[0];
				part = args[1];
			}
			else{
				return result;
			}
			break;
		}
		case "promote":
		case "demote":{
			type = "player";
			part = args[0];
			break;
		}
		default: return result;
		}
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("commands/tab_complete.php", new String[]{
			"type="+URLEncoder.encode(type),
			"part="+URLEncoder.encode(part)
		});
		if(yamlConfiguration==null) return result;
		if(!yamlConfiguration.contains("completions")) return result;
		return yamlConfiguration.getStringList("completions");
	}

}
