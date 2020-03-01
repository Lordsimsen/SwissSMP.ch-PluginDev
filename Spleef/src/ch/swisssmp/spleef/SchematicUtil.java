package ch.swisssmp.spleef;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
 
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.command.util.AsyncCommandBuilder;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;
import com.sk89q.worldedit.util.io.Closer;

public class SchematicUtil {
 
	public static Location save(Player player, String schematicName) {
        try {
			Bukkit.dispatchCommand(player, "/copy"); 
            File schematic = new File(Spleef.getInstance().getDataFolder(), "/schematics/" + schematicName + ".schematic");
            schematic.getParentFile().mkdirs();
            WorldEditPlugin wep = Spleef.worldEditPlugin;
 
            LocalSession localSession = wep.getSession(player);
            ClipboardHolder selection = localSession.getClipboard();
 
            BlockVector3 min = selection.getClipboard().getMinimumPoint();
            BlockVector3 max = selection.getClipboard().getMaximumPoint();
            Region region = new CuboidRegion(max.subtract(min).add(BlockVector3.at(1, 1, 1)), min);
 
            Clipboard clipboard = new BlockArrayClipboard(region);
            clipboard.commit();
            ClipboardFormat format = BuiltInClipboardFormat.MCEDIT_SCHEMATIC;
            SchematicSaveTask task = new SchematicSaveTask(player, schematic, format, selection, true);
            AsyncCommandBuilder.wrap(task, BukkitAdapter.adapt(player));
            return new Location(player.getWorld(), min.getX(), min.getY(), min.getZ());
        } catch (EmptyClipboardException ex) {
            player.sendMessage(ChatColor.RED+"Deine Auswahl ist leer. W�hle zuerst einen Bereich.");
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            player.sendMessage(ChatColor.RED+"Fehler beim Speichern. Der Dateiname '"+schematicName+"' scheint ung�ltig zu sein.");
            return null;
        }
    }
    
	public static boolean paste(String schematicName, Location pasteLoc) {
        try {
        	String fileName = "/schematics/" + schematicName + ".schematic";
        	Debug.Log("Loading schematic from "+fileName);
            File file = new File(Spleef.getInstance().getDataFolder(), fileName);
            if(!file.exists()){
            	Debug.Log("Couldn't load "+fileName);
            	return false;
            }
            if(pasteLoc==null){
            	Debug.Log("The location to paste the schematic is invalid.");
            	return false;
            }
            if(pasteLoc.getWorld()==null){
            	Debug.Log("The world to paste the schematic is invalid.");
            	return false;
            }
            
            ClipboardFormat format = BuiltInClipboardFormat.MCEDIT_SCHEMATIC;
            SchematicLoadTask task = new SchematicLoadTask(null, file, format);
            ClipboardHolder holder = task.call();
            BlockVector3 origin = BlockVector3.at(pasteLoc.getBlockX(), pasteLoc.getBlockY(), pasteLoc.getBlockZ());
            holder.getClipboard().setOrigin(origin);
            PasteBuilder paste = holder.createPaste(holder.getClipboard());
            paste.build();
            return true;
        } catch (MaxChangedBlocksException ex) {
            ex.printStackTrace();
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private static class SchematicSaveTask implements Callable<Void> {
        private final Player player;
        private final File file;
        private final ClipboardFormat format;
        private final ClipboardHolder holder;
        private final boolean overwrite;

        SchematicSaveTask(Player player, File file, ClipboardFormat format, ClipboardHolder holder, boolean overwrite) {
            this.player = player;
            this.file = file;
            this.format = format;
            this.holder = holder;
            this.overwrite = overwrite;
        }

        @Override
        public Void call() throws Exception {
            Clipboard clipboard = holder.getClipboard();
            Transform transform = holder.getTransform();
            Clipboard target;

            // If we have a transform, bake it into the copy
            if (transform.isIdentity()) {
                target = clipboard;
            } else {
                FlattenedClipboardTransform result = FlattenedClipboardTransform.transform(clipboard, transform);
                target = new BlockArrayClipboard(result.getTransformedRegion());
                target.setOrigin(clipboard.getOrigin());
                Operations.completeLegacy(result.copyTo(target));
            }

            try (Closer closer = Closer.create()) {
                FileOutputStream fos = closer.register(new FileOutputStream(file));
                BufferedOutputStream bos = closer.register(new BufferedOutputStream(fos));
                ClipboardWriter writer = closer.register(format.getWriter(bos));
                writer.write(target);

                Debug.Log(player.getName() + " saved " + file.getCanonicalPath() + (overwrite ? " (overwriting previous file)" : ""));
            }
            return null;
        }
    }

    private static class SchematicLoadTask implements Callable<ClipboardHolder> {
        private final Player player;
        private final File file;
        private final ClipboardFormat format;

        SchematicLoadTask(Player player, File file, ClipboardFormat format) {
            this.player = player;
            this.file = file;
            this.format = format;
        }

        @Override
        public ClipboardHolder call() throws Exception {
            try (Closer closer = Closer.create()) {
                FileInputStream fis = closer.register(new FileInputStream(file));
                BufferedInputStream bis = closer.register(new BufferedInputStream(fis));
                ClipboardReader reader = closer.register(format.getReader(bis));

                Clipboard clipboard = reader.read();
                Debug.Log(player.getName() + " loaded " + file.getCanonicalPath());
                return new ClipboardHolder(clipboard);
            }
        }
    }

}
