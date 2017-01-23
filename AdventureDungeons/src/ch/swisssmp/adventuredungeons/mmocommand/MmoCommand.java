package ch.swisssmp.adventuredungeons.mmocommand;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.adventuredungeons.Main;

public class MmoCommand implements CommandExecutor{
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
    	switch(label){
    	case "CraftMmo":
    	case "mmo":
	    	switch(args[0]){
		    	case "help":
		    		displayHelp();
		    		break;
		    	case "reload":
		    		Main.loadYamls();
		    		player.sendMessage("[CraftMMO] Konfiguration neu geladen.");
					break;
		    	case "debug":
		    		Main.debug = !Main.debug;
		    		if(Main.debug){
		    			player.sendMessage(ChatColor.GREEN+"Der CraftMmo Debug-Modus ist nun eingeschaltet.");
		    		}
		    		else {
		    			player.sendMessage(ChatColor.RED+"Der CraftMmo Debug-Modus ist nun ausgeschaltet.");
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
    	player.sendMessage("CraftMMO Version "+Main.pdfFile.getVersion()+" Befehle:");
    	player.sendMessage("/CraftMMO = /mmo");
    	player.sendMessage("-----");
    	player.sendMessage("/mmo help - Zeigt diese Hilfe an");
    	player.sendMessage("/mmo reload - Lädt die Konfigurationen neu");
    }
}
