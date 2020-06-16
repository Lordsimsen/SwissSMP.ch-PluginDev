package ch.swisssmp.zones;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockVector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a type of Zone, Plugins can implement this to register their own types which are then grouped and saved together
 */
public abstract class ZoneType {

    private final NamespacedKey key;
    private final String name;

    protected ZoneType(Plugin plugin, String id, String name){
        this.key = new NamespacedKey(plugin, id);
        this.name = name;
    }

    public NamespacedKey getKey(){
        return key;
    }

    public String getName(){
        return name;
    }

    protected abstract Zone createZone(String name);

    public abstract ZoneMeta createMeta();
    public abstract String getCustomEnum();
    public abstract String getDisplayName(String name);
    public abstract List<String> getItemLore(Zone zone);
    public abstract RegionType getRegionType();

    protected List<String> getDefaultLore(Zone zone){
        List<String> result = new ArrayList<>();
        result.add(ChatColor.GRAY+zone.getCollection().getContainer().getBukkitWorld().getName());
        BlockVector min = zone.getMin();
        BlockVector max = zone.getMax();
        result.add(ChatColor.GRAY+""+min.getBlockX()+","+min.getBlockY()+","+min.getBlockZ()+" - "+max.getBlockX()+","+max.getBlockY()+","+max.getBlockZ());
        return result;
    }
}
