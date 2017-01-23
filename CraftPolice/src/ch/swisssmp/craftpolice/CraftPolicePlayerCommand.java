package ch.swisssmp.craftpolice;

import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import ch.swisssmp.craftpolice.Main;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class CraftPolicePlayerCommand implements CommandExecutor{
	private CommandSender _sender;
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	ConfigurationSection chiefs = Main.chiefs;
    	ConfigurationSection corps = Main.corps;
    	_sender = sender;
    	switch(args[0]){
	    	case "help":
	    		displayHelp();
	    		break;
	    	case "cc":
	    	case "createcorps":
	    		if(args.length<3){
	    			displayHelp();
	    			break;
	    		}
	    		String city = args[1];
	    		String chiefofpolice = args[2];
	    		
	    		PermissionUser permissionuser = PermissionsEx.getUser(chiefofpolice);
	    		if(permissionuser==null){
	    			sendMessage("Spieler "+chiefofpolice+" nicht gefunden!");
	    			break;
	    		}
	    		if(!permissionuser.inGroup(city)){
	    			sendMessage(chiefofpolice+" ist kein Bürger von "+city+"!");
	    			break;
	    		}
	    		if(chiefs!=null){
		    		if(chiefs.contains(chiefofpolice))
		    		{
		    			String chiefscity = chiefs.getConfigurationSection(chiefofpolice).getString("city");
		    			sendMessage(chiefofpolice+" ist bereits Kommandant der Polizei in "+chiefscity);
		    			break;
		    		}
	    		}
	    		ConfigurationSection chiefSection = chiefs.createSection(chiefofpolice);
	    		chiefSection.set("city", city);
	    		permissionuser.addPermission("craftpolice.chiefofpolice");
	    		ConfigurationSection policecorps = corps.createSection(city);
	    		policecorps.set("chief", chiefofpolice);
	    		sendMessage("Neues Polizei Corps in "+city+" mit "+chiefofpolice+" als Polizeikommandant erstellt!");
	    		break;
			case "rc":
			case "deletecorps":
			case "removecorps":
				if(args.length<2){
					displayHelp();
					break;
				}
				city = args[1];
				if(!Main.policecorps.getConfigurationSection("corps").contains(city)){
	    			sendMessage("In "+city+" existiert kein Polizei-Corps");
	    			break;
				}
				policecorps = corps.getConfigurationSection(city);
				if(policecorps==null){
					sendMessage("In der Stadt "+city+" gibt es kein Polizei Corps!");
					break;
				}
				chiefofpolice = policecorps.getString("chief");
				permissionuser = PermissionsEx.getUser(chiefofpolice);
				if(permissionuser!=null)
					permissionuser.removePermission("craftpolice.chiefofpolice");
				List<String> officers = policecorps.getStringList("officers");
				if(officers!=null){
					for(String officer : officers){
						PermissionUser officeruser = PermissionsEx.getUser(officer);
						if(officeruser!=null)
							officeruser.removePermission("craftpolice.officer");
					}
				}
				ConfigurationSection cellSection = policecorps.getConfigurationSection("cells");
				if(cellSection!=null){
					Set<String> cells = cellSection.getKeys(false);
					for(String cellname : cells){
						ConfigurationSection cell = cellSection.getConfigurationSection(cellname);
						PrisonCell.delete(cell);
					}
				}
				chiefs.set(chiefofpolice, null);
				corps.set(city, null);
				sendMessage("Polizei Corps in "+city+" mit "+chiefofpolice+" als Polizeikommandant gelöscht!");
				permissionuser.addPermission("craftpolice.chiefofpolice");
				break;
			case "ec":
			case "editcorps":
				if(args.length < 3){
					displayHelp();
					break;
				}
				city = args[1];
				chiefofpolice = args[2];
				PermissionUser chiefUser = PermissionsEx.getUser(chiefofpolice);
				if(chiefUser==null){
					sendMessage("Der Spieler "+chiefofpolice+" wurde nicht gefunden!");
					break;
				}
				if(!corps.contains(city)){
					sendMessage("Diese Stadt hat kein Polizei Corps!");
					break;
				}
				//at this point everything will be executed
				ConfigurationSection citySection = corps.getConfigurationSection(city);
				String oldchiefofpolice = citySection.getString("chief");
				if(chiefs.contains(oldchiefofpolice)){
					//if the old chief is found he will be removed
					chiefUser = PermissionsEx.getUser(oldchiefofpolice);
					if(chiefUser!=null)
						chiefUser.removePermission("craftpolice.chiefofpolice");
					chiefs.set(oldchiefofpolice, null);
				}
				chiefSection = chiefs.createSection(chiefofpolice);
				chiefSection.set("city", city);
				chiefUser.addPermission("craftpolice.chiefofpolice");
				citySection.set("chief", chiefofpolice);
				break;
		}
    	Main.saveYamls();
		return true;
	}
    public void displayHelp(){
		sendMessage("CraftPolice Version "+Main.pdfFile.getVersion()+" Befehle:");
		sendMessage("/CraftPolice = /cp");
		sendMessage("-----");
		sendMessage("/cp createcorps [Stadt] [Polizeikommandant]");
		sendMessage("/cp removecorps [Stadt]");
		sendMessage("/cp editcorps [Stadt] [Polizeikommandant(neu)]");
		sendMessage("/cp help");
    }
    private void sendMessage(String message){
		_sender.sendMessage(message);
    }
}
