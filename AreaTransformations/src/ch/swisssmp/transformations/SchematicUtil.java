package ch.swisssmp.transformations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
 
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldguard.WorldGuard;

import ch.swisssmp.webcore.WebCore;
 
/**
*         @author BlahBerrys (eballer48) - ericballard7@gmail.com
*
*         An easy-to-use API for saving, loading, and pasting WorldEdit/MCEdit
*         schematics. (Built against WorldEdit 6.1)
*
*/
 
public class SchematicUtil {
 
    public static Location save(Player player, String transformation_id, String schematicName) {
        try {
			Bukkit.dispatchCommand(player, "/copy"); 
            File schematic = new File(AreaTransformations.getInstance().getDataFolder(), "/schematics/" + transformation_id + "/" + schematicName + ".schematic");
            File dir = new File(AreaTransformations.getInstance().getDataFolder(), "/schematics/" + transformation_id + "/");
            if (!dir.exists())
                dir.mkdirs();
 
            WorldEditPlugin wep = AreaTransformations.worldEditPlugin;
 
            LocalSession localSession = wep.getSession(player);
            ClipboardHolder selection = localSession.getClipboard();
 
            BlockVector3 min = selection.getClipboard().getMinimumPoint();
            BlockVector3 max = selection.getClipboard().getMaximumPoint();
 
            CuboidRegion region = new CuboidRegion(max.subtract(min).add(BlockVector3.ONE), min);
            BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(region.getWorld(), -1);
            
            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
            forwardExtentCopy.setCopyingEntities(true);
            Operations.complete(forwardExtentCopy);
            
            ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(schematic));
        	writer.write(clipboard);
            return new Location(player.getWorld(), min.getX(), min.getY(), min.getZ());
        } catch (EmptyClipboardException ex) {
            player.sendMessage(ChatColor.RED+"Deine Auswahl ist leer. Wähle zuerst einen Bereich.");
        } catch (WorldEditException e) {
        	player.sendMessage(ChatColor.RED+"Fehler beim Speichern.");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
        	player.sendMessage(ChatColor.RED+"Fehler beim Speichern. (Datei nicht gefunden)");
			e.printStackTrace();
		} catch (IOException e) {
        	player.sendMessage(ChatColor.RED+"Fehler beim Speichern. (Dateisystemfehler)");
			e.printStackTrace();
		}
		return null;
    }
 
 
    public static boolean paste(String schematicName, Location pasteLoc) {
        try {
        	String fileName = "/schematics/" + schematicName;
        	WebCore.info("[AreaTransformations] Loading schematic from "+fileName);
            File file = new File(AreaTransformations.getInstance().getDataFolder(), fileName);
            
            com.sk89q.worldedit.world.World world = WorldGuard.getInstance().getPlatform().getWorldByName(pasteLoc.getWorld().getName());
 
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            ClipboardReader reader = format.getReader(new FileInputStream(file));
            Clipboard clipboard = reader.read();
            
            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(pasteLoc.getX(), pasteLoc.getY(), pasteLoc.getZ()))
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } catch (MaxChangedBlocksException ex) {
            ex.printStackTrace();
            return false;
        } catch (WorldEditException e) {
			e.printStackTrace();
			return false;
		}
    }
 
}