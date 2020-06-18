package ch.swisssmp.zones;

import ch.swisssmp.zones.editor.selection.PointSelector;
import ch.swisssmp.zones.editor.visualization.VisualizationColorScheme;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    public abstract String getCustomEnum();
    public abstract String getDisplayName(String name);
    public abstract List<String> getItemLore(Zone zone);
    public abstract RegionType getRegionType();
    public abstract PointSelector createSelector(Zone zone);
    public abstract VisualizationColorScheme getVisualizationColorScheme();

    protected List<String> getDefaultLore(Zone zone){
        List<String> result = new ArrayList<>();
        result.add(ChatColor.GRAY+zone.getCollection().getContainer().getBukkitWorld().getName());
        BlockVector min = zone.getMin();
        BlockVector max = zone.getMax();
        result.add(ChatColor.GRAY+""+min.getBlockX()+","+min.getBlockY()+","+min.getBlockZ()+" bis "+max.getBlockX()+","+max.getBlockY()+","+max.getBlockZ());
        return result;
    }

    protected VisualizationColorScheme getDefaultVisualizationColorScheme(){
        return new VisualizationColorScheme(Color.YELLOW, Color.GREEN, Color.RED);
    }

    public static Optional<ZoneType> get(NamespacedKey key){
        return ZoneTypes.getType(key);
    }

    public static Collection<ZoneType> getAll(){
        return ZoneTypes.getAll();
    }

    public static Optional<ZoneType> findByName(String name){
        String key = name.toLowerCase();
        return ZoneTypes.getAll().stream().filter(t->t.name.toLowerCase().contains(key)).findAny();
    }
}
