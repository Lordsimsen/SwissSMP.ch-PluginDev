package ch.swisssmp.adventuredungeons.mmocommand;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.regions.Region;

import ch.swisssmp.adventuredungeons.Main;
import ch.swisssmp.adventuredungeons.mmoblock.MmoBlock;
import ch.swisssmp.adventuredungeons.mmoworld.MmoRegion;
import ch.swisssmp.adventuredungeons.mmoworld.MmoWorld;
import ch.swisssmp.adventuredungeons.mmoworld.MmoWorldInstance;
import ch.swisssmp.adventuredungeons.util.MmoResourceManager;

public class MmoRegionCommand implements CommandExecutor{
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
	    	case "reload":
				try {
					boolean fullload = false;
					if(args.length>1){
						fullload = (args[1].equals("all"));
					}
					MmoWorldInstance worldInstance = MmoWorld.getInstance(player.getWorld());
					MmoRegion.loadRegions(worldInstance, fullload);
		    		player.sendMessage("[CraftMMO] Regionen-Konfiguration neu geladen.");
				} catch (Exception e) {
					player.sendMessage("Fehler beim laden der Daten! Mehr Details in der Konsole...");
					e.printStackTrace();
				}
				break;
	    	case "list":
	    		for(MmoWorldInstance worldInstance : MmoWorld.instances.values()){
		    		for(MmoRegion region : worldInstance.regions.values()){
		    			player.sendMessage("- ("+worldInstance.world.getName()+") "+region.region_name+": "+region.name);
		    		}
	    		}
	    		break;
	    	case "register":
	    	case "unregister":{
	    		if(args.length<3){
	    			player.sendMessage(ChatColor.RED+"Material definieren");
	    			break;
	    		}
	    		String region_id = args[1];
	    		String mc_materialdata = args[2].toUpperCase();
	    		LocalSession session = Main.worldEditPlugin.getSession(player);
	    		Region region;
	    		String action;
	    		if(args[0].equals("register")){
	    			action = "SET";
	    		}
	    		else action = "DELETE";
	    		
	    		boolean success = true;
				int success_count = 0;
				int match_count = 0;
				Main.debug("Starting registration process");
				try{
					region = session.getSelection(session.getSelectionWorld());
				}
				catch(Exception e){
					region = null;
				}
				if(region==null){
					player.sendMessage(ChatColor.RED+"Zuerst einen Bereich auswählen. Es werden nur Blöcke innerhalb von der Auswahl verändert. Tipp: Eine ganze Region mit /rg select [region] direkt anwählen. (Region-Name im Online-Tool)");
					break;
				}
	    		World world = player.getWorld();
				Main.debug("mc_materialdata is "+mc_materialdata);
				Main.debug("Region from "+region.getMinimumPoint().toString()+" to "+region.getMaximumPoint().toString());
	    		for(BlockVector blockVector : region){
	    			if(blockVector==null){
						Main.debug("Found a blockVector==null");
	    				continue;
	    			}
	    			int x = (int) blockVector.getX();
	    			int y = (int) blockVector.getY();
	    			int z = (int) blockVector.getZ();
	    			Location location = new Location(world, x, y, z);
	    			Block block = world.getBlockAt(location);
	    			if(block==null){
						Main.debug("Found a block==null at "+x+", "+y+", "+z);
	    				continue;
	    			}
	    			if(MmoBlock.getMaterialString(block, mc_materialdata.contains(":")).equals(mc_materialdata)){
	    				match_count++;
	    				MmoWorldInstance worldInstance = MmoWorld.getInstance(block);
			    		String response = MmoResourceManager.getResponse("treasureeditor.php", new String[]{"region="+region_id, "x="+x, "y="+y, "z="+z, "material="+mc_materialdata, "action="+action, "world="+worldInstance.system_name});
			    		
			    		if(response.equals("1")){
			    			success_count++;
			    		}
			    		else if(response.equals("0"))
			    			continue;
			    		else{
			    			Main.debug(response);
			    			success = false;
			    			break;
			    		}
	    			}
	    		}
    			String actionLabel = "registriert oder angepasst";
    			if(args[0].equals("unregister")){
    				actionLabel = "entfernt";
    			}
    			player.sendMessage(ChatColor.GRAY+"In dieser Region sind "+match_count+" "+mc_materialdata+".");
	    		if(success){
	    			player.sendMessage(ChatColor.GREEN+""+success_count+" Blöcke "+actionLabel+".");
	    		}
	    		else{
	    			player.sendMessage(ChatColor.RED+"Fehler beim abarbeiten der Blöcke. Es wurden bereits "+success_count+" Blöcke "+actionLabel+".");
	    		}
	    		break;
	    	}
		}
		return true;
	}
    public void displayHelp(){
    	player.sendMessage("/MmoRegion = /mmoregion");
    	player.sendMessage("-----");
    	player.sendMessage("/mmoregion help - Zeigt diese Hilfe an");
    	player.sendMessage("/mmoregion reload - Lädt die Konfigurationen neu");
    }
}
