package ch.swisssmp.craftmmo.mmocommand;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmocamp.MmoCamp;
import ch.swisssmp.craftmmo.mmocamp.MmoCampEditor;
import ch.swisssmp.craftmmo.mmoworld.MmoWorld;
import ch.swisssmp.craftmmo.mmoworld.MmoWorldInstance;
import net.md_5.bungee.api.ChatColor;

public class MmoCampCommand implements CommandExecutor{
	Player player;
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(sender instanceof Player)
    		player = (Player) sender;
    	else{
    		Main.info("Can only be executed from within the game.");
    		return true;
    	}
    	if(args.length==0){
    		displayHelp();
    		return true;
    	}
    	switch(args[0]){
	    	case "help":
	    		displayHelp();
	    		break;
	    	case "countmobs":
	    		if(args.length>=2){
	    			try{
	    				String string_id = args[1];
	    				if(!NumberUtils.isNumber(string_id)){
	    					player.sendMessage(ChatColor.RED+args[1]+" ist keine gültige ID!");
	    					break;
	    				}
	    				Integer mmo_camp_id = Integer.parseInt(args[1]);
	    				MmoWorldInstance worldInstance = MmoWorld.getInstance(player);
	    				MmoCamp camp = worldInstance.getCamp(mmo_camp_id);
	    				if(camp!=null){
	    					player.sendMessage(ChatColor.GREEN+"Mobs im Camp: "+camp.getMobCount());
	    				}
	    				else{
		    				player.sendMessage(ChatColor.RED+"Konnte die Mobs im Camp "+args[1]+" nicht zählen!");
	    				}
	    			}
    				catch(Exception e){
	    				player.sendMessage(ChatColor.RED+"Konnte die Mobs im Camp "+args[1]+" nicht zählen!");
    				}
	    		}
	    		break;
	    	case "activate":
	    		if(args.length>=2){
	    			try{
	    				String string_id = args[1];
	    				if(!NumberUtils.isNumber(string_id)){
	    					player.sendMessage(ChatColor.RED+args[1]+" ist keine gültige ID!");
	    					break;
	    				}
	    				Integer mmo_camp_id = Integer.parseInt(args[1]);
	    				MmoWorldInstance worldInstance = MmoWorld.getInstance(player);
	    				MmoCamp camp = worldInstance.getCamp(mmo_camp_id);
	    				if(camp!=null){
	    					camp.activate();
	    					player.sendMessage(ChatColor.GREEN+"Camp aktiviert!");
	    				}
	    				else{
		    				player.sendMessage(ChatColor.RED+"Konnte "+args[1]+" nicht aktivieren!");
	    				}
	    			}
    				catch(Exception e){
	    				player.sendMessage(ChatColor.RED+"Konnte "+args[1]+" nicht aktivieren!");
    				}
	    			break;
	    		}
	    	case "deactivate":
	    		if(args.length>=2){
	    			try{
	    				String string_id = args[1];
	    				if(!NumberUtils.isNumber(string_id)){
	    					player.sendMessage(ChatColor.RED+args[1]+" ist keine gültige ID!");
	    					break;
	    				}
	    				Integer mmo_camp_id = Integer.parseInt(args[1]);
	    				MmoWorldInstance worldInstance = MmoWorld.getInstance(player);
	    				MmoCamp camp = worldInstance.getCamp(mmo_camp_id);
	    				if(camp!=null){
	    					camp.deactivate();
	    					player.sendMessage(ChatColor.GREEN+"Camp deaktiviert!");
	    				}
	    				else{
		    				player.sendMessage(ChatColor.RED+"Konnte "+args[1]+" nicht deaktivieren!");
	    				}
	    			}
    				catch(Exception e){
	    				player.sendMessage(ChatColor.RED+"Konnte "+args[1]+" nicht deaktivieren!");
    				}
	    			break;
	    		}
	    	case "reload":
				try {
    				MmoWorldInstance worldInstance = MmoWorld.getInstance(player);
					MmoCamp.loadCamps(worldInstance);
		    		player.sendMessage("[CraftMMO] Camp-Konfiguration neu geladen.");
				} catch (Exception e) {
					player.sendMessage("Fehler beim laden der Daten! Mehr Details in der Konsole...");
					e.printStackTrace();
				}
	    		break;
	    	case "edit":
	    	case "editor":{
    			MmoCampEditor editor = MmoCampEditor.get(player);
	    		if(args.length<2){
	    			player.sendMessage("/mmocamp editor [spawnpoint_id]");
	    			break;
	    		}
	    		else if(args[1].equals("quit")){
	    			if(editor!=null) editor.quit();
	    			player.sendMessage(ChatColor.GRAY+"Editor beendet.");
	    			break;
	    		}
	    		ItemStack hand = player.getInventory().getItemInMainHand();
	    		if(hand!=null && hand.getType()!=Material.GOLD_BLOCK){
	    			player.sendMessage(ChatColor.RED+"Bitte einen Goldblock verwenden.");
	    			break;
	    		}
	    		int mmo_spawnpoint_id = Integer.parseInt(args[1]);
	    		if(editor!=null){
	    			player.sendMessage(ChatColor.GRAY+"Editor für Spawnpunkt "+editor.mmo_spawnpoint_id+" beendet.");
	    			editor.mmo_spawnpoint_id = mmo_spawnpoint_id;
	    		}
	    		else{
		    		new MmoCampEditor(player, hand, mmo_spawnpoint_id);
	    		}
    			player.sendMessage(ChatColor.GREEN+"Editor für Spawnpunkt "+mmo_spawnpoint_id+" gestartet.");
	    		break;
	    	}
		}
		return true;
	}
    public void displayHelp(){
    	player.sendMessage("/MmoItem = /mmoitem");
    	player.sendMessage("-----");
    	player.sendMessage("/mmoitem help - Zeigt diese Hilfe an");
    	player.sendMessage("/mmoitem reload - Lädt alle Camps neu");
    	player.sendMessage("/mmoitem debug - Schaltet den Debug-Modus ein oder aus");
    }
}
