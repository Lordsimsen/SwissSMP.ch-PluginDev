package ch.swisssmp.adventuredungeons.command;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.adventuredungeons.AdventureDungeons;

public class AdventureDungeonsCommand implements CommandExecutor{
	Player player;
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(sender instanceof Player)
    		player = (Player) sender;
    	else{
    		AdventureDungeons.info("Can only be executed from within the game.");
    		return true;
    	}
    	if(args.length==0){
    		displayHelp();
    		return true;
    	}
    	switch(label){
    	case "AdventureDungeons":
    	case "mmo":
	    	switch(args[0]){
		    	case "help":
		    		displayHelp();
		    		break;
		    	case "reload":
		    		AdventureDungeons.loadYamls();
		    		player.sendMessage("[AdventureDungeons] Konfiguration neu geladen.");
					break;
		    	case "debug":
		    		AdventureDungeons.debug = !AdventureDungeons.debug;
		    		if(AdventureDungeons.debug){
		    			player.sendMessage(ChatColor.GREEN+"Der Debug-Modus ist nun eingeschaltet.");
		    		}
		    		else {
		    			player.sendMessage(ChatColor.RED+"Der Debug-Modus ist nun ausgeschaltet.");
		    		}
		    		break;
			}
	    	break;
    	case "rename":
    		PlayerInventory inventory = player.getInventory();
    		ItemStack itemStack = inventory.getItemInMainHand();
    		if(itemStack!=null && args.length>0){
    			ItemMeta itemMeta = itemStack.getItemMeta();
    			itemMeta.setDisplayName(args[0]);
    			itemStack.setItemMeta(itemMeta);
    		}
    		break;
    	}
		return true;
	}
    public void displayHelp(){
    	player.sendMessage("CraftMMO Version "+AdventureDungeons.pdfFile.getVersion()+" Befehle:");
    	player.sendMessage("/CraftMMO = /mmo");
    	player.sendMessage("-----");
    	player.sendMessage("/mmo help - Zeigt diese Hilfe an");
    	player.sendMessage("/mmo reload - L�dt die Konfigurationen neu");
    }
}
