package ch.swisssmp.craftelytra;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.session.ClipboardHolder;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("Can only be executed from within the game. Sorry!");
			return true;
		}
		Player player = (Player) sender;

        WorldEditPlugin wep = CraftElytra.worldEditPlugin;

        LocalSession localSession = wep.getSession(player);
        ClipboardHolder selection;
		try {
			selection = localSession.getClipboard();
		} catch (EmptyClipboardException e) {
			// TODO Auto-generated catch block
			sender.sendMessage("Deine Auswahl ist leer. Wähle zuerst mit WorldEdit einen Bereich aus.");
			return true;
		}
        Vector min = selection.getClipboard().getMinimumPoint();
        Vector max = selection.getClipboard().getMaximumPoint();
        String direction = "n";
        if(args!=null && args.length>0){
        	switch(args[0]){
        	case "n":
        		break;
        	case "e":
        		direction = "e";
        		break;
        	case "s":
        		direction = "s";
        		break;
        	case "w":
        		direction = "w";
        		break;
    		default:
    			break;
        	}
        }
        String schematicName = "template_"+direction;
        Schematic schematic = new Schematic(min, max, schematicName);
        schematic.save(player.getWorld());
        sender.sendMessage("Vorlage gespeichert.");
		return true;
	}

}
