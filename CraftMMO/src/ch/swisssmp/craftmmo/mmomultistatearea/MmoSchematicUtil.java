package ch.swisssmp.craftmmo.mmomultistatearea;

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

import ch.swisssmp.craftmmo.Main;
 
/**
*         @author BlahBerrys (eballer48) - ericballard7@gmail.com
*
*         An easy-to-use API for saving, loading, and pasting WorldEdit/MCEdit
*         schematics. (Built against WorldEdit 6.1)
*
*/
 
@SuppressWarnings("deprecation")
public class MmoSchematicUtil {
 
    public static Location save(Player player, String mmo_multistatearea_id, String schematicName) {
        try {
			Bukkit.dispatchCommand(player, "/copy"); 
            File schematic = new File(Main.dataFolder, "/schematics/" + mmo_multistatearea_id + "/" + schematicName + ".schematic");
            File dir = new File(Main.dataFolder, "/schematics/" + mmo_multistatearea_id + "/");
            if (!dir.exists())
                dir.mkdirs();
 
            WorldEditPlugin wep = Main.worldEditPlugin;
 
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
        	String fileName = "/schematics/" + schematicName;
        	Main.info("Loading schematic from "+fileName);
            File file = new File(Main.dataFolder, fileName);
 
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