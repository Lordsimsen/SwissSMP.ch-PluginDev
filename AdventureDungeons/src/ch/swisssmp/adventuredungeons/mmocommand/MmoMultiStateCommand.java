package ch.swisssmp.adventuredungeons.mmocommand;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.adventuredungeons.Main;
import ch.swisssmp.adventuredungeons.mmomultistatearea.MmoAreaState;
import ch.swisssmp.adventuredungeons.mmomultistatearea.MmoMultiStateArea;
import ch.swisssmp.adventuredungeons.mmomultistatearea.MmoSchematicUtil;
import ch.swisssmp.adventuredungeons.mmoworld.MmoWorld;
import ch.swisssmp.adventuredungeons.mmoworld.MmoWorldInstance;
import ch.swisssmp.webcore.DataSource;

public class MmoMultiStateCommand implements CommandExecutor{
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(args.length==0){
    		displayHelp(sender);
    		return true;
    	}
    	switch(args[0]){
	    	case "help":
	    		displayHelp(sender);
	    		break;
	    	case "reload":
				try {
					boolean fullload = false;
					if(args.length>1){
						fullload = (args[1].equals("all"));
					}
					for(MmoWorldInstance worldInstance : MmoWorld.instances.values()){
						MmoMultiStateArea.loadTransformations(worldInstance, fullload);
					}
		    		sender.sendMessage("[CraftMMO] Transformations-Konfiguration neu geladen.");
				} catch (Exception e) {
					sender.sendMessage("Fehler beim laden der Daten! Mehr Details in der Konsole...");
					e.printStackTrace();
				}
				break;
	    	case "list":
				for(MmoWorldInstance worldInstance : MmoWorld.instances.values()){
		    		for(MmoMultiStateArea area : worldInstance.transformations.values()){
		    			for(MmoAreaState schematic : area.schematics.values())
		    				sender.sendMessage("("+worldInstance.world.getName()+") ["+area.mmo_multistatearea_id+"] "+area.name+": "+schematic.schematicName);
		    		}
				}
	    		break;
	    	case "register":
	    	case "unregister":{
	    		Player player;
	    		if(sender instanceof Player)
	    			player = (Player)sender;
	    		else{
	    			sender.sendMessage("Kann nur ingame ausgeführt werden.");
	    			return true;
	    		}
	    		if(args.length<3){
	    			sender.sendMessage(ChatColor.RED+"Namen angeben");
	    			break;
	    		}
	    		String mmo_multistatearea_id = args[1];
	    		if(!StringUtils.isNumeric(mmo_multistatearea_id)){
	    			sender.sendMessage(ChatColor.RED+"mmo_multistatearea_id entspricht der ID aus dem Web-Tool.");
	    			return true;
	    		}
	    		String schematicName = args[2];
	    		String action;
	    		Location location;
	    		if(args[0].equals("register")){
	    			action = "SET";
	    			location = MmoSchematicUtil.save(player, mmo_multistatearea_id, schematicName);
	    			if(location==null){
	    				break;
	    			}
	    		}
	    		else{
	    			File oldFile = new File(Main.dataFolder, "/schematics/" + schematicName);
	    			if(oldFile.exists())
	    				oldFile.delete();
	    			action = "DELETE";
	    			location = player.getLocation();
	    		}
	    		
	    		String response = DataSource.getResponse("multistateeditor.php", new String[]{
	    			"multistatearea="+mmo_multistatearea_id,
	    			"schematic="+schematicName,
	    			"action="+action,
	    			"world="+MmoWorld.getInstance(location).system_name,
	    			"x="+(int)Math.floor(location.getX()),
	    			"y="+(int)Math.floor(location.getY()),
	    			"z="+(int)Math.floor(location.getZ()),
	    		});
	    		
	    		boolean success = response.equals("1");
				Main.debug("Starting schematic registration process");
				
    			String actionLabel = "registriert. Einstellungen im Web-Tool vornehmen und danach '/mmomultistate reload' verwenden";
    			if(args[0].equals("unregister")){
    				actionLabel = "gelöscht. '/mmomultistate reload' verwenden, damit die Änderungen sofort angewendet werden";
    			}
	    		if(success){
	    			player.sendMessage(ChatColor.GREEN+"Transformation "+actionLabel+".");
	    		}
	    		else{
	    			player.sendMessage(ChatColor.RED+"Fehler beim bearbeiten der Transformation.");
	    		}
	    		break;
	    	}
	    	case "trigger":
	        	if(!(sender instanceof Player)) return true;
	        	Player player = (Player) sender;
	        	MmoWorldInstance worldInstance = MmoWorld.getInstance(player);
	    		if(args.length<3){
	    			sender.sendMessage(ChatColor.RED+"mmo_multistatearea_id und Namen angeben");
	    			break;
	    		}
	    		else if(worldInstance==null){
	    			sender.sendMessage("Diese Welt ist keine Mmo-Welt.");
	    			return true;
	    		}
	    		if(!StringUtils.isNumeric(args[1])){
	    			sender.sendMessage(ChatColor.RED+"mmo_multistatearea_id entspricht der ID aus dem Web-Tool.");
	    			return true;
	    		}
	    		int mmo_multistatearea_id = Integer.parseInt(args[1]);
	    		String schematicName = args[2];
	    		
	    		MmoMultiStateArea area = worldInstance.getTransformation(mmo_multistatearea_id);
	    		if(area==null){
	    			sender.sendMessage(ChatColor.RED+"Transformationsgruppe nicht gefunden.");
	    			break;
	    		}
	    		MmoAreaState areaState = area.schematics.get(schematicName);
	    		if(areaState==null){
	    			sender.sendMessage(ChatColor.RED+"Transformation nicht gefunden.");
	    			break;
	    		}
	    		if(areaState.trigger(player)){
	    			String triggerMessage = areaState.triggerMessage;
	    			if(triggerMessage==null){
	    				sender.sendMessage(ChatColor.GREEN+areaState.schematicName+" ausgelöst.");
	    			}
	    			else if(areaState.triggerMessage.equals("")){
	    				sender.sendMessage(ChatColor.GREEN+areaState.schematicName+" ausgelöst.");
	    			}
	    		}
	    		else{
    				sender.sendMessage(ChatColor.RED+areaState.schematicName+" konnte nicht ausgelöst werden.");
	    		}
	    		break;
		}
		return true;
	}
    public void displayHelp(CommandSender sender){
    	sender.sendMessage("/MmoMultiState = /mmomultistate, /multistate");
    	sender.sendMessage("-----");
    	sender.sendMessage("/multistate help - Zeigt diese Hilfe an");
    	sender.sendMessage("/multistate reload - Lädt die Konfigurationen neu");
    	sender.sendMessage("/multistate register [mmo_multistate_id] [name] - Registriert eine Transformation");
    	sender.sendMessage("/multistate unregister [mmo_multistate_id] [name] - Löscht eine Transformation");
    }
}
