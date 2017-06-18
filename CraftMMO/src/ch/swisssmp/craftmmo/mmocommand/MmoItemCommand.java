package ch.swisssmp.craftmmo.mmocommand;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmoitem.MmoItem;
import ch.swisssmp.craftmmo.mmoitem.MmoItemManager;
import ch.swisssmp.craftmmo.mmoitem.MmoItemclass;
import ch.swisssmp.craftmmo.mmoplayer.MmoQuestbook;
import ch.swisssmp.craftmmo.mmoshop.MmoShop;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MmoItemCommand implements CommandExecutor{
	Player player;
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	label = label.toLowerCase();
    	if(sender instanceof Player)
    		player = (Player) sender;
    	else{
    		Main.info("Can only be executed from within the game.");
    		return true;
    	}
    	switch(label){
    	case "mmoshop":
    	case "mmoitem":
    	switch(args[0]){
	    	case "help":
	    		displayHelp();
	    		break;
	    	case "summon":
	    		if(args.length>=2){
    				ItemStack item;
					try {
						item = MmoItemManager.getItemStack(args[1]);
						if(item==null){
							player.sendMessage("Item "+args[1]+" nicht gefunden.");
							break;
						}
    					player.getInventory().addItem(item);
					} catch (Exception e) {
						player.sendMessage("Item "+args[1]+" nicht gefunden.");
						e.printStackTrace();
					}
	    		}
	    		break;
	    	case "reload":
				try {
					if(label.equals("mmoitem")){
						MmoItemclass.loadClasses(true);
			    		player.sendMessage("[CraftMMO] Item-Konfiguration neu geladen.");
			    		for(Player player : Bukkit.getOnlinePlayers()){
			    			MmoItemManager.updateInventory(player.getInventory());
			    		}
					}
					else if(label.equals("mmoshop")){
						MmoShop.loadShops();
			    		player.sendMessage("[CraftMMO] Shop-Konfiguraton neu geladen.");
					}
				} catch (Exception e) {
					player.sendMessage("Fehler beim laden der Daten! Mehr Details in der Konsole...");
					e.printStackTrace();
				}
				break;
	    	case "id":{
	    		if(!label.equals("mmoitem")){
	    			break;
	    		}
	    		ItemStack itemStack = player.getInventory().getItemInMainHand();
	    		int itemID = MmoItem.getID(itemStack);
	    		if(itemID<0){
	    			player.sendMessage("Dieses Item wird nicht von CraftMMO gesteuert.");
	    		}
	    		else player.sendMessage("Item ID: "+itemID);
	    		break;
	    	}
	    	case "info":{
	    		if(!label.equals("mmoitem")){
	    			break;
	    		}
	    		ItemStack itemStack = player.getInventory().getItemInMainHand();
	    		NBTTagCompound saveData = MmoItem.getSaveData(itemStack);
	    		if(saveData!=null){
	    			if(args.length==1){
	    				for(String key : saveData.c()){
	    					player.sendMessage(key+": "+saveData.get(key).toString());
	    				}
	    			}
	    			else{
	    				String attributeName = args[1];
	    				if(saveData.hasKey(attributeName)){
	    					player.sendMessage(attributeName+": "+saveData.get(attributeName).toString());
	    				}
	    				else{
	    					player.sendMessage("Das Item hat kein Attribut "+args[1]);
	    				}
	    			}
	    		}
	    		else player.sendMessage("Keine NBT Daten vorhanden.");
	    		break;
	    	}
		}
    		break;
    	case "q":
    	case "quest":
    	case "quests":
    		if(args.length>0){
    			if(args[0].equals("reload")){
    				MmoQuestbook.load(player.getUniqueId());
    				MmoQuestbook.relinkInstance(player.getUniqueId(), true);
    				return true;
    			}
    			else{
    				player.sendMessage("Unbekannter Befehl '"+args[0]+"'");
    			}
    		}
    		MmoQuestbook.toggle(player.getUniqueId());
    		break;
    	}
		return true;
	}
    public void displayHelp(){
    	player.sendMessage("/MmoItem = /mmoitem = /mmoshop");
    	player.sendMessage("-----");
    	player.sendMessage("/mmoitem help - Zeigt diese Hilfe an");
    	player.sendMessage("/mmoitem summon [typ] [name] - Generiert ein MMO-Item");
    	player.sendMessage("/mmoitem id - Gibt die CraftMMO-ID des ausgewählten Items aus");
    }
}
