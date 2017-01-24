package ch.swisssmp.spleef;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
 
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.session.ClipboardHolder;

@SuppressWarnings("deprecation")
public class SchematicUtil {
 
	public static Location save(Player player, String schematicName) {
        try {
			Bukkit.dispatchCommand(player, "/copy"); 
            File schematic = new File(Spleef.dataFolder, "/schematics/" + schematicName + ".schematic");
            schematic.getParentFile().mkdirs();
            WorldEditPlugin wep = Spleef.worldEditPlugin;
 
            LocalSession localSession = wep.getSession(player);
            ClipboardHolder selection = localSession.getClipboard();
            EditSession editSession = wep.createEditSession(player);
 
            Vector min = selection.getClipboard().getMinimumPoint();
            Vector max = selection.getClipboard().getMaximumPoint();
 
            editSession.enableQueue();
            CuboidClipboard clipboard = new CuboidClipboard(max.subtract(min).add(new Vector(1, 1, 1)), min);
            clipboard.copy(editSession);
            SchematicFormat.MCEDIT.save(clipboard, schematic);
            editSession.flushQueue();
            return new Location(player.getWorld(), min.getX(), min.getY(), min.getZ());
        } catch (IOException | DataException ex) {
            ex.printStackTrace();
            player.sendMessage(ChatColor.RED+"Fehler beim Speichern. Der Dateiname '"+schematicName+"' scheint ungültig zu sein.");
            return null;
        } catch (EmptyClipboardException ex) {
            player.sendMessage(ChatColor.RED+"Deine Auswahl ist leer. Wähle zuerst einen Bereich.");
            return null;
        }
    }
    
	public static boolean paste(String schematicName, Location pasteLoc) {
        try {
        	String fileName = "/schematics/" + schematicName + ".schematic";
        	Spleef.info("Loading schematic from "+fileName);
            File file = new File(Spleef.dataFolder, fileName);
            if(!file.exists()){
            	Spleef.info("Couldn't load "+fileName);
            	return false;
            }
            if(pasteLoc==null){
            	Spleef.debug("The location to paste the schematic is invalid.");
            	return false;
            }
            if(pasteLoc.getWorld()==null){
            	Spleef.debug("The world to paste the schematic is invalid.");
            	return false;
            }
            EditSession editSession = new EditSession(new BukkitWorld(pasteLoc.getWorld()), 999999999);
            editSession.enableQueue();
 
            SchematicFormat schematic = SchematicFormat.getFormat(file);
            CuboidClipboard clipboard = schematic.load(file);
 
            clipboard.paste(editSession, BukkitUtil.toVector(pasteLoc), false);
            editSession.flushQueue();
            return true;
        } catch (DataException | IOException ex) {
            ex.printStackTrace();
            return false;
        } catch (MaxChangedBlocksException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
