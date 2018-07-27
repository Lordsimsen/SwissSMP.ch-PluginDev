package ch.swisssmp.towercontrol.transformations;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.towercontrol.Arena;
import ch.swisssmp.towercontrol.TowerControl;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class TransformationCommand implements CommandExecutor{
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(args==null || args.length==0){
    		return false;
    	}
    	switch(args[0]){
    		case "aktualisiere":
    		case "aktualisieren":
	    	case "reload":
				try {
					for(Arena arena : Arena.getArenas()){
						arena.loadTransformations();
					}
		    		sender.sendMessage("[TowerControl] Transformations-Konfiguration neu geladen.");
				} catch (Exception e) {
					sender.sendMessage("[TowerControl] Fehler beim laden der Daten! Mehr Details in der Konsole...");
					e.printStackTrace();
				}
				break;
	    	case "auflisten":
	    	case "list":{
	    		int count = 0;
				for(Arena arena : Arena.getArenas()){
		    		for(TransformationArea area : arena.getTransformations()){
		    			for(AreaState schematic : area.getSchematics()){
		    				count++;
		    				sender.sendMessage("("+area.getWorld().getName()+") ["+area.getTransformationId()+"] "+area.getName()+": "+schematic.getSchematicName());
		    			}
		    		}
				}
				if(count==0){
	    			sender.sendMessage("[TowerControl] Aktuell sind keine Transformationen geladen.");
				}
	    		break;
	    	}
	    	case "erstellen":
	    	case "erstelle":
	    	case "entfernen":
	    	case "entferne":
	    	case "register":
	    	case "unregister":{
	    		Player player;
	    		if(sender instanceof Player)
	    			player = (Player)sender;
	    		else{
	    			sender.sendMessage("Can only be used from within the game");
	    			return true;
	    		}
	    		if(args.length<3){
	    			sender.sendMessage("[TowerControl]"+ChatColor.RED+" Transformations-Enum und Namen angeben");
	    			break;
	    		}
	    		Arena arena = Arena.get(player.getWorld());
	    		if(arena==null){
	    			sender.sendMessage("[TowerControl]"+ChatColor.RED+" Du kannst nur in einer Arena Transformationen erstellen.");
	    			return true;
	    		}
	    		String transformation_enum = args[1].toUpperCase();
	    		TransformationArea transformationArea = arena.getTransformation(transformation_enum);
	    		if(transformationArea==null){
	    			sender.sendMessage("[TowerControl]"+ChatColor.RED+" Transformation '"+transformation_enum+"' nicht gefunden.");
	    			return true;
	    		}
	    		String schematicName = args[2];
	    		String action;
	    		Location location;
	    		if(args[0].equals("register")||args[0].equals("erstellen")||args[0].equals("erstelle")){
	    			action = "SET";
	    			location = SchematicUtil.save(player, transformation_enum, schematicName);
	    			if(location==null){
	    				break;
	    			}
	    		}
	    		else{
	    			File oldFile = new File(TowerControl.getPlugin().getDataFolder(), "/schematics/" + schematicName);
	    			if(oldFile.exists())
	    				oldFile.delete();
	    			action = "DELETE";
	    			location = player.getLocation();
	    		}
	    		
    			YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("transformations/editor.php", new String[]{
	    			"transformation="+transformationArea.getTransformationId(),
	    			"schematic="+schematicName,
	    			"action="+action,
	    			"world="+URLEncoder.encode(location.getWorld().getName()),
	    			"x="+(int)Math.floor(location.getX()),
	    			"y="+(int)Math.floor(location.getY()),
	    			"z="+(int)Math.floor(location.getZ()),
	    		});
	    		
	    		boolean success = (yamlConfiguration!=null && yamlConfiguration.contains("success"));
				
    			String actionLabel = "registriert. Einstellungen im Web-Tool vornehmen und danach '/transformation aktualisieren' verwenden";
    			if(args[0].equals("unregister")){
    				actionLabel = "gelöscht. '/transformation aktualisieren' verwenden, damit die Änderungen sofort angewendet werden";
    			}
	    		if(success){
	    			player.sendMessage("[TowerControl]"+ChatColor.GREEN+" Transformation "+actionLabel+".");
	    		}
	    		else{
	    			player.sendMessage("[TowerControl]"+ChatColor.RED+" Fehler beim bearbeiten der Transformation.");
	    		}
	    		break;
	    	}
	    	case "setze":
	    	case "setzen":
	    	case "trigger":
	    		Player player;
	    		if(sender instanceof Player)
	    			player = (Player)sender;
	    		else{
	    			sender.sendMessage("Can only be used from within the game");
	    			return true;
	    		}
	    		if(args.length<3){
	    			sender.sendMessage("[TowerControl]"+ChatColor.RED+" Transformations-Enum und Zustand angeben");
	    			break;
	    		}
	    		Arena arena = Arena.get(player.getWorld());
	    		if(arena==null){
	    			sender.sendMessage("[TowerControl]"+ChatColor.RED+" Du kannst nur in einer Arena Transformationen ändern.");
	    			return true;
	    		}
	    		String schematicName = args[2];
	    		
	    		TransformationArea area = arena.getTransformation(args[1]);
	    		if(area==null){
	    			sender.sendMessage("[TowerControl]"+ChatColor.RED+" Transformationsgruppe nicht gefunden.");
	    			break;
	    		}
	    		AreaState areaState = area.getSchematic(schematicName);
	    		if(areaState==null){
	    			sender.sendMessage("[TowerControl]"+ChatColor.RED+" Transformation nicht gefunden.");
	    			break;
	    		}
    			if(areaState.trigger()){
    				sender.sendMessage("[TowerControl]"+ChatColor.GREEN+" "+areaState.getSchematicName()+" ausgelöst.");
    			}
	    		else{
    				sender.sendMessage("[TowerControl]"+ChatColor.RED+" "+areaState.getSchematicName()+" konnte nicht ausgelöst werden.");
	    		}
	    		break;
	    	default:
	    		return false;
		}
		return true;
	}
}
