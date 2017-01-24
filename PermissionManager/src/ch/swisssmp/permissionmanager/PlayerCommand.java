package ch.swisssmp.permissionmanager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import ch.swisssmp.commandscheduler.CommandScheduler;
import ch.swisssmp.webcore.DataSource;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null) return false;
		if(args.length<1) return false;
		switch(args[0]){
			case "reload":{
				PermissionManager.loadPermissions();
				sender.sendMessage(ChatColor.GREEN+"Erfolg! Berechtigungen aktualisiert.");
				break;
			}
			case "user":{
				if(args.length<2) {
					sender.sendMessage("/permission user [user] [action]");
					return true;
				}
				if(args.length<3){
					PluginCommand pexCommand = PermissionManager.permissionsExPlugin.getCommand("pex");
					if(pexCommand!=null){
						pexCommand.execute(sender, "pex", args);
					}
				}
				else{
					String user = args[1];
					String action = args[2];
					if((action.equals("add")||action.equals("remove"))&&args.length>3){
						try {
							DataSource.getResponse("permissions/"+action+"_permission.php", new String[]{
									"user="+URLEncoder.encode(user, "utf-8"),
									"permission="+args[3]
							});
							PermissionManager.loadPermissions();
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
					else if(args.length>3){
						sender.sendMessage(ChatColor.RED+"Nur 'add' und 'remove' möglich.");
					}
					else{
						sender.sendMessage(ChatColor.RED+"Keinen Spieler definiert.");
					}
				}
				break;
			}
			case "group":{
				if(args.length<2) {
					sender.sendMessage("/permission group [group] [action]");
					return true;
				}
				if(args.length<3 || (args.length>=3 && args[2].equals("users"))){
					PluginCommand pexCommand = PermissionManager.permissionsExPlugin.getCommand("pex");
					if(pexCommand!=null){
						pexCommand.execute(sender, "pex", args);
					}
				}
				else{
					String group = args[1];
					String action = args[2];
					if((action.equals("add")||action.equals("remove"))&&args.length>3){
						try {
							DataSource.getResponse("permissions/"+action+"_permission.php", new String[]{
									"group="+URLEncoder.encode(group, "utf-8"),
									"permission="+args[3]
							});
							PermissionManager.loadPermissions();
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
					else if(args.length>3){
						sender.sendMessage(ChatColor.RED+"Nur 'add' und 'remove' möglich.");
					}
					else{
						sender.sendMessage(ChatColor.RED+"Keinen Spieler definiert.");
					}
				}
				break;
			}
			case "promote":{
				if(args.length<2) {
					sender.sendMessage("/permission promote [user]");
					return true;
				}
				else{
					String user = args[1];
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
				}
				break;
			}
			case "demote":{
				if(args.length<2) {
					sender.sendMessage("/permission demote [user]");
					return true;
				}
				else{
					String user = args[1];
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
				}
				break;
			}
		}
		return true;
	}

}
