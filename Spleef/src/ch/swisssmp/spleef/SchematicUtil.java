package ch.swisssmp.spleef;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;

public class SchematicUtil {
 
	public static Location save(Player player, String schematicName) {
        try {
			Bukkit.dispatchCommand(player, "/copy"); 
            File schematicFile = new File(Spleef.getInstance().getDataFolder(), "/schematics/" + schematicName + ".schematic");
            schematicFile.getParentFile().mkdirs();
            WorldEditPlugin wep = Spleef.worldEditPlugin;
 
            LocalSession localSession = wep.getSession(player);
            ClipboardHolder selection = localSession.getClipboard();

            BlockVector3 min = selection.getClipboard().getMinimumPoint();
            BlockVector3 max = selection.getClipboard().getMaximumPoint();
            BlockVector3 result = BlockVector3.at(Math.min(min.getBlockX(), max.getBlockX()), Math.min(min.getBlockY(),max.getBlockY()),Math.min(min.getBlockZ(), max.getBlockZ()));
            
            try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(schematicFile))) {
                writer.write(selection.getClipboard());
            }
            catch(Exception e) {
            	e.printStackTrace();
            }
            
            return new Location(player.getWorld(), result.getX(), result.getY(), result.getZ());
        } catch (EmptyClipboardException ex) {
            player.sendMessage(ChatColor.RED+"Deine Auswahl ist leer. Wähle zuerst einen Bereich.");
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            player.sendMessage(ChatColor.RED+"Fehler beim Speichern. Der Dateiname '"+schematicName+"' scheint ungültig zu sein.");
            return null;
        }
    }
    
	public static boolean paste(String schematicName, Location pasteLoc) {
        try {
        	String fileName = "/schematics/" + schematicName + ".schematic";
        	// Bukkit.getLogger().info("Loading schematic from "+fileName);
            File file = new File(Spleef.getInstance().getDataFolder(), fileName);
            if(!file.exists()){
            	Bukkit.getLogger().info("Couldn't load "+fileName);
            	return false;
            }
            if(pasteLoc==null){
            	Bukkit.getLogger().info("The location to paste the schematic is invalid.");
            	return false;
            }
            if(pasteLoc.getWorld()==null){
            	Bukkit.getLogger().info("The world to paste the schematic is invalid.");
            	return false;
            }
            
            Clipboard clipboard;

            ClipboardFormat format = ClipboardFormats.findByFile(file);
            try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                clipboard = reader.read();
            }
            /* use the clipboard here */
            com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(pasteLoc.getWorld());
            int x = pasteLoc.getBlockX();
            int y = pasteLoc.getBlockY();
            int z = pasteLoc.getBlockZ();

            clipboard.setOrigin(BlockVector3.at(x, y, z));
            // Bukkit.getLogger().info("Paste at "+x+", "+y+", "+z);
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(x, y, z))
                        // configure here
                        .build();
                Operations.complete(operation);
            }

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
