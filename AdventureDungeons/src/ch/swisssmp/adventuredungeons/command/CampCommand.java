package ch.swisssmp.adventuredungeons.command;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
	    	case "trigger":{
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
    					camp.activate();
    					sender.sendMessage(ChatColor.GREEN+"Camp ausgelöst!");
    				}
    				else{
    					sender.sendMessage(ChatColor.RED+"Konnte "+args[1]+" nicht auslösen!");
    				}
    			}
				catch(Exception e){
					sender.sendMessage(ChatColor.RED+"Konnte "+args[1]+" nicht auslösen!");
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
    					camp.deactivate();
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
					Camp.loadCamps(dungeonInstance);
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
	    		if((args.length<3 && editor!=null) || (args.length>=3 && StringUtils.isNumeric(args[2]) && editor.getCampId()==Integer.parseInt(args[2]))){
	    			editor.quit();
	    			return true;
	    		}
	    		if(args.length<3) return false;
	    		if(!StringUtils.isNumeric(args[2])) return false;
	    		int camp_id = Integer.parseInt(args[1]);
	    		if(editor!=null){
	    			editor.quit();
	    		}
	    		CampEditor campEditor = CampEditor.initiate(player, camp_id);
    			if(campEditor!=null) player.sendMessage(ChatColor.GREEN+"Editor für Camp "+campEditor.getName()+" gestartet.");
	    		break;
	    	}
		}
		return true;
	}
}
