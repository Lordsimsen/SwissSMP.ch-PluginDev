package ch.swisssmp.transformations;

import java.io.File;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class PlayerCommand implements CommandExecutor{
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(args==null || args.length==0){
    		return false;
    	}
    	switch(args[0]){
	    	case "reload":
				try {
					for(TransformationWorld transformationWorld : TransformationWorld.getWorlds()){
						transformationWorld.loadTransformations();
					}
		    		sender.sendMessage("[AreaTransformations] Transformations-Konfiguration neu geladen.");
				} catch (Exception e) {
					sender.sendMessage("[AreaTransformations] Fehler beim laden der Daten! Mehr Details in der Konsole...");
					e.printStackTrace();
				}
				break;
	    	case "list":{
	    		int count = 0;
				for(TransformationWorld transformationWorld : TransformationWorld.getWorlds()){
		    		for(TransformationArea area : transformationWorld.getTransformations()){
		    			for(AreaState schematic : area.getSchematics()){
		    				count++;
		    				sender.sendMessage("("+area.getWorld().getName()+") ["+area.getTransformationEnum()+"] "+area.getName()+": "+schematic.getSchematicName());
		    			}
		    		}
				}
				if(count==0){
	    			sender.sendMessage("[AreaTransformations] Aktuell sind keine Transformationen geladen.");
				}
	    		break;
	    	}
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
	    			sender.sendMessage("[AreaTransformations]"+ChatColor.RED+" Transformations-ID und Namen angeben");
	    			break;
	    		}
	    		String transformation_id = args[1];
	    		if(!StringUtils.isNumeric(transformation_id)){
	    			sender.sendMessage("[AreaTransformations]"+ChatColor.RED+" Transformations-ID muss eine gültige ID aus dem Web-Interface sein.");
	    			return true;
	    		}
	    		String schematicName = args[2];
	    		String action;
	    		Location location;
	    		if(args[0].equals("register")){
	    			action = "SET";
	    			location = SchematicUtil.save(player, transformation_id, schematicName);
	    			if(location==null){
	    				break;
	    			}
	    		}
	    		else{
	    			File oldFile = new File(AreaTransformations.getInstance().getDataFolder(), "/schematics/" + schematicName);
	    			if(oldFile.exists())
	    				oldFile.delete();
	    			action = "DELETE";
	    			location = player.getLocation();
	    		}
	    		
    			YamlConfiguration yamlConfiguration = DataSource.getYamlResponse(AreaTransformations.getInstance(), "editor.php", new String[]{
	    			"transformation="+transformation_id,
	    			"schematic="+schematicName,
	    			"action="+action,
	    			"world="+URLEncoder.encode(location.getWorld().getName()),
	    			"x="+(int)Math.floor(location.getX()),
	    			"y="+(int)Math.floor(location.getY()),
	    			"z="+(int)Math.floor(location.getZ()),
	    		});
	    		
	    		boolean success = (yamlConfiguration!=null && yamlConfiguration.contains("success"));
				
    			String actionLabel = "registriert. Einstellungen im Web-Tool vornehmen und danach '/transformation reload' verwenden";
    			if(args[0].equals("unregister")){
    				actionLabel = "gelöscht. '/transformation reload' verwenden, damit die Änderungen sofort angewendet werden";
    			}
	    		if(success){
	    			player.sendMessage("[AreaTransformations]"+ChatColor.GREEN+" Transformation "+actionLabel+".");
	    		}
	    		else{
	    			player.sendMessage("[AreaTransformations]"+ChatColor.RED+" Fehler beim bearbeiten der Transformation.");
	    		}
	    		break;
	    	}
	    	case "trigger":
	    		Player player = null;
	    		if(args.length<4){
	    			sender.sendMessage("[AreaTransformations]"+ChatColor.RED+" Welt, Transformations-ID und Zustand angeben");
	    			break;
	    		}
	    		if(args.length>4){
	    			String playerString = args[3];
	    			player = Bukkit.getPlayer(playerString);
	    			if(player==null) player = Bukkit.getPlayer(UUID.fromString(playerString));
	    			if(player==null){
	    				sender.sendMessage("[AreaTransformations] Spieler "+playerString+" nicht gefunden, Prozess wird ohne Spieler-Referenz weitergeführt.");
	    			}
	    		}
	    		World world = Bukkit.getWorld(args[1]);
	    		if(world==null){
	    			sender.sendMessage("[AreaTransformations]"+ChatColor.RED+" Welt "+args[0]+" nicht gefunden.");
	    			return true;
	    		}
	    		TransformationWorld transformationWorld = TransformationWorld.get(world);
	    		String schematicName = args[3];
	    		TransformationArea area;
	    		if(StringUtils.isNumeric(args[2])){
		    		int transformation_id = Integer.parseInt(args[2]);
		    		area = transformationWorld.getTransformation(transformation_id);
	    		}
	    		else{
	    			area = transformationWorld.getTransformation(args[2]);
	    		}
	    		if(area==null){
	    			sender.sendMessage("[AreaTransformations]"+ChatColor.RED+" Transformationsgruppe nicht gefunden.");
	    			break;
	    		}
	    		AreaState areaState = area.getSchematic(schematicName);
	    		if(areaState==null){
	    			sender.sendMessage("[AreaTransformations]"+ChatColor.RED+" Transformation nicht gefunden.");
	    			break;
	    		}
	    		if(player==null){
	    			if(areaState.trigger()){
	    				sender.sendMessage("[AreaTransformations]"+ChatColor.GREEN+" "+areaState.getSchematicName()+" ausgelöst.");
	    			}
	    			else{
	    				sender.sendMessage("[AreaTransformations]"+ChatColor.RED+" "+areaState.getSchematicName()+" konnte nicht ausgelöst werden.");
	    			}
	    		}
	    		else{
	    			if(areaState.trigger(player)){
	    				sender.sendMessage("[AreaTransformations]"+ChatColor.GREEN+" "+areaState.getSchematicName()+" ausgelöst.");
	    			}
		    		else{
	    				sender.sendMessage("[AreaTransformations]"+ChatColor.RED+" "+areaState.getSchematicName()+" konnte nicht ausgelöst werden.");
		    		}
	    		}
	    		break;
	    	case "debug":{
	    		AreaTransformations.debug = !AreaTransformations.debug;
	    		if(AreaTransformations.debug){
	    			sender.sendMessage("[AreaTransformations]"+ChatColor.GREEN+" Der Debug-Modus wurde aktiviert.");
	    		}
	    		else{
	    			sender.sendMessage("[AreaTransformations]"+ChatColor.RED+" Der Debug-Modus wurde deaktiviert.");
	    		}
	    		break;
	    	}
	    	default:
	    		return false;
		}
		return true;
	}
}
