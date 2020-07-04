package ch.swisssmp.transformations;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.swisssmp.webcore.HTTPRequest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import org.bukkit.util.StringUtil;

public class TransformationCommand implements TabExecutor {
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
    	if(args==null || args.length==0){
    		return false;
    	}
    	/*
    	switch(args[0]){
	    	case "reload":
				try {
					for(TransformationContainer transformationContainer : TransformationContainer.getWorlds()){
						transformationContainer.loadTransformations();
					}
		    		sender.sendMessage("[AreaTransformations] Transformations-Konfiguration neu geladen.");
				} catch (Exception e) {
					sender.sendMessage("[AreaTransformations] Fehler beim laden der Daten! Mehr Details in der Konsole...");
					e.printStackTrace();
				}
				break;
	    	case "list":{
	    		int count = 0;
				for(TransformationContainer transformationContainer : TransformationContainer.getWorlds()){
		    		for(AreaTransformation area : transformationContainer.getTransformations()){
		    			for(TransformationState schematic : area.getSchematics()){
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

	    		HTTPRequest request = DataSource.getResponse(AreaTransformations.getInstance(), "editor.php", new String[]{
						"transformation="+transformation_id,
						"schematic="+schematicName,
						"action="+action,
						"world="+URLEncoder.encode(location.getWorld().getName()),
						"x="+(int)Math.floor(location.getX()),
						"y="+(int)Math.floor(location.getY()),
						"z="+(int)Math.floor(location.getZ()),
				});
	    		request.onFinish(()->{
					YamlConfiguration yamlConfiguration = request.getYamlResponse();

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
				});
	    		return true;
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
	    		TransformationContainer transformationContainer = TransformationContainer.get(world);
	    		String schematicName = args[3];
	    		AreaTransformation area;
	    		if(StringUtils.isNumeric(args[2])){
		    		int transformation_id = Integer.parseInt(args[2]);
		    		area = transformationContainer.getTransformation(transformation_id);
	    		}
	    		else{
	    			area = transformationContainer.getTransformation(args[2]);
	    		}
	    		if(area==null){
	    			sender.sendMessage("[AreaTransformations]"+ChatColor.RED+" Transformationsgruppe nicht gefunden.");
	    			break;
	    		}
	    		TransformationState transformationState = area.getState(schematicName);
	    		if(transformationState ==null){
	    			sender.sendMessage("[AreaTransformations]"+ChatColor.RED+" Transformation nicht gefunden.");
	    			break;
	    		}
	    		if(player==null){
	    			if(transformationState.trigger()){
	    				sender.sendMessage("[AreaTransformations]"+ChatColor.GREEN+" "+ transformationState.getSchematicName()+" ausgelöst.");
	    			}
	    			else{
	    				sender.sendMessage("[AreaTransformations]"+ChatColor.RED+" "+ transformationState.getSchematicName()+" konnte nicht ausgelöst werden.");
	    			}
	    		}
	    		else{
	    			if(transformationState.trigger(player)){
	    				sender.sendMessage("[AreaTransformations]"+ChatColor.GREEN+" "+ transformationState.getSchematicName()+" ausgelöst.");
	    			}
		    		else{
	    				sender.sendMessage("[AreaTransformations]"+ChatColor.RED+" "+ transformationState.getSchematicName()+" konnte nicht ausgelöst werden.");
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
		}*/
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if(args.length==0){
			List<String> options = Arrays.asList("create", "trigger");
			String current = args.length>0 ? args[0] : "";
			return StringUtil.copyPartialMatches(current, options, new ArrayList<>());
		}

		if(args.length<=2){
			String current = args.length>1 ? args[1] : "";
			Pattern pattern = Pattern.compile("^.*/.*$");
			Matcher matcher = pattern.matcher(current);
			if(matcher.matches()){

			}
			else{
				List<String> options = Bukkit.getWorlds().stream().map(w->w.getName()+"/").collect(Collectors.toList());
				World defaultWorld = sender instanceof Player ? ((Player)sender).getWorld() : null;
				if(defaultWorld!=null){
					TransformationContainer container = TransformationContainer.get(defaultWorld);
					options.addAll(container.getTransformations().stream().map(t->t.getUniqueId().toString()).collect(Collectors.toList()));
				}
			}
		}

		//if(args.length<=3)
		//Stream<AreaTransformation> transformations = Arrays.stream(container.getTransformations());
		//List<String> options = .map(t->t.)
		return null;
	}
}
