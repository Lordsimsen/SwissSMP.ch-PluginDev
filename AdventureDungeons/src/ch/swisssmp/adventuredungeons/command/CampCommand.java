package ch.swisssmp.adventuredungeons.command;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.camp.Camp;
import ch.swisssmp.adventuredungeons.camp.CampEditor;
import ch.swisssmp.adventuredungeons.world.Dungeon;
import ch.swisssmp.adventuredungeons.world.DungeonInstance;
import net.md_5.bungee.api.ChatColor;

public class CampCommand implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(args==null || args.length==0){
    		return false;
    	}
    	switch(args[0]){
    	case "list":{
    		DungeonInstance dungeonInstance;
    		Player player = null;
    		if(args.length>1){
    			if(!StringUtils.isNumeric(args[1])) return false;
    			dungeonInstance = Dungeon.getInstance(Integer.valueOf(args[1]));
    		}
    		else if(sender instanceof Player){
    			player = (Player) sender;
    			dungeonInstance = Dungeon.getInstance(player);
    		}
    		else return false;
    		for(Camp camp : dungeonInstance.getCamps()){
    			sender.sendMessage("["+camp.getCampId()+"] "+camp.getName()+" ("+camp.getMobCount()+" Mobs)");
    		}
    		break;
    	}
    	case "trigger":{
    		if(args.length<2) return false;
    		DungeonInstance dungeonInstance;
    		Player player = null;
    		if(args.length>2){
    			if(!StringUtils.isNumeric(args[2])) return false;
    			dungeonInstance = Dungeon.getInstance(Integer.valueOf(args[2]));
    		}
    		else if(sender instanceof Player){
    			player = (Player) sender;
    			dungeonInstance = Dungeon.getInstance(player);
    		}
    		else return false;
			try{
				String string_id = args[1];
				if(!NumberUtils.isNumber(string_id)){
					sender.sendMessage(ChatColor.RED+args[1]+" ist keine gültige ID!");
					break;
				}
				Integer camp_id = Integer.parseInt(args[1]);
				Camp camp = dungeonInstance.getCamp(camp_id);
				if(camp!=null){
					camp.trigger(player, sender instanceof Player);
					if(AdventureDungeons.debug) sender.sendMessage(ChatColor.GREEN+"Camp ausgelöst!");
				}
				else{
					sender.sendMessage("[AdventureDungeons]"+ChatColor.RED+" Camp "+args[1]+" nicht gefunden!");
				}
			}
			catch(Exception e){
				e.printStackTrace();
				sender.sendMessage("[AdventureDungeons]"+ChatColor.RED+" Konnte "+args[1]+" nicht auslösen!");
			}
			break;
    	}
    	case "clear":{
    		if(args.length<2) return false;
    		DungeonInstance dungeonInstance;
    		if(args.length>2){
    			if(!StringUtils.isNumeric(args[2])) return false;
    			dungeonInstance = Dungeon.getInstance(Integer.valueOf(args[2]));
    		}
    		else if(sender instanceof Player){
    			dungeonInstance = Dungeon.getInstance((Player)sender);
    		}
    		else return false;
			try{
				String string_id = args[1];
				if(!NumberUtils.isNumber(string_id)){
					sender.sendMessage(ChatColor.RED+args[1]+" ist keine gültige ID!");
					break;
				}
				Integer mmo_camp_id = Integer.parseInt(args[1]);
				Camp camp = dungeonInstance.getCamp(mmo_camp_id);
				if(camp!=null){
					camp.despawnMobs();
					sender.sendMessage(ChatColor.GREEN+"Camp geleert!");
				}
				else{
					sender.sendMessage(ChatColor.RED+"Konnte "+args[1]+" nicht leeren!");
				}
			}
			catch(Exception e){
				sender.sendMessage(ChatColor.RED+"Konnte "+args[1]+" nicht leeren!");
			}
			break;
    	}
    	case "reload":{
    		DungeonInstance dungeonInstance;
    		if(args.length>1){
    			if(!StringUtils.isNumeric(args[2])) return false;
    			dungeonInstance = Dungeon.getInstance(Integer.valueOf(args[2]));
    		}
    		else if(sender instanceof Player){
    			dungeonInstance = Dungeon.getInstance((Player)sender);
    		}
    		else return false;
			try {
				dungeonInstance.loadCamps();
				sender.sendMessage("[AdventureDungeons] Camp-Konfiguration neu geladen.");
			} catch (Exception e) {
				sender.sendMessage("Fehler beim laden der Daten! Mehr Details in der Konsole...");
				e.printStackTrace();
			}
    		break;
    	}
    	case "spawnpoints":{
    		if(!(sender instanceof Player)){
    			sender.sendMessage("[AdventureDungeons] Editor kann nur ingame verwendet werden.");
    			return true;
    		}
    		Player player = (Player) sender;
			CampEditor editor = CampEditor.get(player);
    		if((args.length<2 && editor!=null) || (args.length>=2 && editor!=null && StringUtils.isNumeric(args[1]) && editor.getCampId()==Integer.parseInt(args[1]))){
    			editor.quit();
    			return true;
    		}
    		if(args.length<2) return false;
    		if(!StringUtils.isNumeric(args[1])) return false;
    		int camp_id = Integer.parseInt(args[1]);
    		if(editor!=null){
    			editor.quit();
    		}
    		CampEditor.initiate(player, camp_id);
    		break;
    	}
    	case "tp":{
    		if(!(sender instanceof Player)){
    			sender.sendMessage("[AdventureDungeons] Editor kann nur ingame verwendet werden.");
    			return true;
    		}
    		if(args.length<2) return false;
    		Player player = (Player) sender;
    		DungeonInstance dungeonInstance = Dungeon.getInstance(player);
    		if(dungeonInstance==null) return true;
    		try{
    			int camp_id = Integer.parseInt(args[1]);
    			Camp camp = dungeonInstance.getCamp(camp_id);
    			if(camp.getLiveEntities().length>0){
    				sender.sendMessage("[AdventureDungeons] Verbleibende Einheit: "+camp.getLiveEntity().getName());
    				player.teleport(camp.getLiveEntity());
    			}
    		}
    		catch(Exception e){
    			sender.sendMessage("[AdventureDungeons] Ungültige Camp-ID "+args[1]);
    			return true;
    		}
    		break;
    	}
    	default: return false;
		}
		return true;
	}
}
