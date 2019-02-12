package ch.swisssmp.permissionmanager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.commandscheduler.CommandScheduler;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import net.md_5.bungee.api.ChatColor;

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
				case "check":{
					if(args.length<3) return false;
					String playerName = args[1];
					String permission = args[2];
					Player player = Bukkit.getPlayer(playerName);
					if(player==null){
						sender.sendMessage("[PermissionManager] "+playerName+" nicht gefunden.");
						return true;
					}
					boolean hasPermission = player.hasPermission(permission);
					if(hasPermission){
						sender.sendMessage("[PermissionManager] "+player.getDisplayName()+ChatColor.RESET+ChatColor.GREEN+" hat die Berechtigung '"+permission+"'.");
					}
					else{
						sender.sendMessage("[PermissionManager] "+player.getDisplayName()+ChatColor.RESET+ChatColor.RED+" hat die Berechtigung '"+permission+"' nicht.");
					}
					break;
				}
				case "debug":{
					PermissionManager.debug = !PermissionManager.debug;
					if(PermissionManager.debug){
						sender.sendMessage("[PermissionManager] Der Debug-Modus ist nun aktiviert.");
					}
					else{
						sender.sendMessage("[PermissionManager] Der Debug-Modus ist nun deaktiviert.");
					}
					break;
				}
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
						HTTPRequest request = DataSource.getResponse(PermissionManager.getInstance(), "user_info.php", new String[]{"user="+URLEncoder.encode(args[1])});
						request.onFinish(()->{
							YamlConfiguration yamlConfiguration = request.getYamlResponse();
							if(yamlConfiguration==null) return;
							if(!yamlConfiguration.contains("message")) return;
							for(String line : yamlConfiguration.getStringList("message"))
								sender.sendMessage(line);
						});
						break;
					}
					case "city":{
						if(args.length<2) {
							sender.sendMessage("/permission city [city]");
							return true;
						}
						HTTPRequest request = DataSource.getResponse(PermissionManager.getInstance(), "city_info.php", new String[]{"city="+URLEncoder.encode(args[1])});
						request.onFinish(()->{
							YamlConfiguration yamlConfiguration = request.getYamlResponse();
							if(yamlConfiguration==null) return;
							if(!yamlConfiguration.contains("message")) return;
							for(String line : yamlConfiguration.getStringList("message"))
								sender.sendMessage(line);
						});
						break;
					}
					case "rank":{
						if(args.length<2) {
							sender.sendMessage("/permission rank [rank]");
							return true;
						}
						HTTPRequest request = DataSource.getResponse(PermissionManager.getInstance(), "rank_info.php", new String[]{"rank="+URLEncoder.encode(args[1])});
						request.onFinish(()->{
							YamlConfiguration yamlConfiguration = request.getYamlResponse();
							if(yamlConfiguration==null) return;
							if(!yamlConfiguration.contains("message")) return;
							for(String line : yamlConfiguration.getStringList("message"))
								sender.sendMessage(line);
						});
						break;
					}
				}
				break;
			}
			case "promote":{
				String user = args[0];
				String senderName = "console";
				if(sender instanceof Player){
					senderName = ((Player)sender).getUniqueId().toString();
				}
				HTTPRequest request = DataSource.getResponse(PermissionManager.getInstance(), "promote.php", new String[]{
						"user="+URLEncoder.encode(user),
						"promoter="+URLEncoder.encode(senderName)
				});
				request.onFinish(()->{
					sender.sendMessage(request.getResponse());
					CommandScheduler.runCommands();
				});
				break;
			}
			case "demote":{
				String user = args[0];
				String senderName = "console";
				if(sender instanceof Player){
					senderName = ((Player)sender).getUniqueId().toString();
				}
				HTTPRequest request = DataSource.getResponse(PermissionManager.getInstance(), "demote.php", new String[]{
						"user="+URLEncoder.encode(user),
						"promoter="+URLEncoder.encode(senderName)
				});
				request.onFinish(()->{
					sender.sendMessage(request.getResponse());
					CommandScheduler.runCommands();
				});
				break;
			}
		}
		return true;
	}

}
