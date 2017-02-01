package ch.swisssmp.permissionmanager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.commandscheduler.CommandScheduler;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null) return false;
		if(args.length<1) return false;
		switch(label){
			case "pex":
			case "perm":
			case "permission":
			{
				switch(args[0]){
					case "reload":{
						if(args.length>1){
							Player player = Bukkit.getPlayer(args[1]);
							if(player!=null){
								PermissionManager.loadPermissions(player);
								sender.sendMessage("Berechtigungen von "+player.getName()+" aktualisiert.");
								break;
							}
						}
						PermissionManager.loadPermissions();
						sender.sendMessage("Berechtigungen aktualisiert.");
						break;
					}
					case "user":{
						if(args.length<2) {
							sender.sendMessage("/permission user [user]");
							return true;
						}
						try {
							YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("permissions/user_info.php", new String[]{"user="+URLEncoder.encode(args[1], "utf-8")});
							if(yamlConfiguration==null) return true;
							if(!yamlConfiguration.contains("message")) return true;
							for(String line : yamlConfiguration.getStringList("message"))
								sender.sendMessage(line);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						break;
					}
					case "city":{
						if(args.length<2) {
							sender.sendMessage("/permission city [city]");
							return true;
						}
						try {
							YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("permissions/city_info.php", new String[]{"city="+URLEncoder.encode(args[1], "utf-8")});
							if(yamlConfiguration==null) return true;
							if(!yamlConfiguration.contains("message")) return true;
							for(String line : yamlConfiguration.getStringList("message"))
								sender.sendMessage(line);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						break;
					}
					case "rank":{
						if(args.length<2) {
							sender.sendMessage("/permission rank [rank]");
							return true;
						}
						try {
							YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("permissions/rank_info.php", new String[]{"rank="+URLEncoder.encode(args[1], "utf-8")});
							if(yamlConfiguration==null) return true;
							if(!yamlConfiguration.contains("message")) return true;
							for(String line : yamlConfiguration.getStringList("message"))
								sender.sendMessage(line);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						break;
					}
				}
				break;
			}
			case "promote":{
				String user = args[0];
				try {
					String senderName = "console";
					if(sender instanceof Player){
						senderName = ((Player)sender).getUniqueId().toString();
					}
					sender.sendMessage(DataSource.getResponse("permissions/promote.php", new String[]{
							"user="+URLEncoder.encode(user, "utf-8"),
							"promoter="+URLEncoder.encode(senderName, "utf-8")
					}));
					CommandScheduler.runCommands();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
			}
			case "demote":{
				String user = args[0];
				try {
					String senderName = "console";
					if(sender instanceof Player){
						senderName = ((Player)sender).getUniqueId().toString();
					}
					sender.sendMessage(DataSource.getResponse("permissions/demote.php", new String[]{
							"user="+URLEncoder.encode(user, "utf-8"),
							"promoter="+URLEncoder.encode(senderName, "utf-8")
					}));
					CommandScheduler.runCommands();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		return true;
	}

}
