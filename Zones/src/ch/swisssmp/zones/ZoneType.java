package ch.swisssmp.zones;

import ch.swisssmp.world.WorldManager;
import ch.swisssmp.zones.editor.selection.PointSelector;
import ch.swisssmp.zones.editor.visualization.VisualizationColorScheme;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
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
    public abstract PointSelector createSelector(Player player, Zone zone);
    public abstract VisualizationColorScheme getVisualizationColorScheme();
    public abstract boolean isInternal();

    protected List<String> getDefaultLore(Zone zone){
        List<String> result = new ArrayList<>();
        result.add(ChatColor.GRAY+getName());
        if (zone == null) {
            return result;
        }

        World world = zone.getCollection().getContainer().getBukkitWorld();
        result.add("");
        result.add(ChatColor.GRAY+"Welt:");
        result.add(" "+ChatColor.GREEN+ WorldManager.getDisplayName(world));
        result.add(ChatColor.GRAY+"Gebiet:");
        BlockVector min = zone.getMin();
        BlockVector max = zone.getMax();
        if(min!=null && max!=null){
            result.add(" "+ChatColor.GREEN+"Von x"+min.getBlockX()+(getRegionType()!=RegionType.POLYGON ? ", y"+min.getBlockY() : "")+", z"+min.getBlockZ());
            result.add(" "+ChatColor.GREEN+"Bis x"+max.getBlockX()+(getRegionType()!=RegionType.POLYGON ? ", y"+max.getBlockY() : "")+", z"+max.getBlockZ());
        }
        else{
            result.add("  "+ChatColor.YELLOW+"Nicht eingezeichnet");
        }
        return result;
    }

    protected VisualizationColorScheme getDefaultVisualizationColorScheme(){
        return new VisualizationColorScheme(Color.YELLOW, Color.YELLOW, Color.RED);
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
