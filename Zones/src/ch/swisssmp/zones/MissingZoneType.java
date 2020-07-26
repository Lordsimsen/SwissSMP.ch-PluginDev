package ch.swisssmp.zones;

import ch.swisssmp.zones.editor.selection.PointSelector;
import ch.swisssmp.zones.editor.visualization.VisualizationColorScheme;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public class MissingZoneType extends ZoneType {

    private static MissingZoneType instance;

    private MissingZoneType(Plugin plugin, String id, String name) {
        super(plugin, id, name);
    }

    @Override
    public String getCustomEnum() {
        return "UNKNOWN_ZONE";
    }

    @Override
    public String getDisplayName(String name) {
        return ChatColor.RESET+name;
    }

    @Override
    public List<String> getItemLore(Zone zone) {
        return Arrays.asList(ChatColor.RED+getName(),"",ChatColor.GRAY+"Zone nicht geladen",ChatColor.GRAY+"oder gelöscht.");
    }

    @Override
    public RegionType getRegionType() {
        return RegionType.GLOBAL;
    }

    @Override
    public PointSelector createSelector(Player player, Zone zone) {
        return null;
    }

    @Override
    public VisualizationColorScheme getVisualizationColorScheme() {
        return null;
    }
    @Override
    public boolean isInternal(){return true;}

    protected static MissingZoneType initialize(Plugin plugin){
        MissingZoneType result = new MissingZoneType(plugin, "missing", "Unbekannte Zone");
        instance = result;
        return result;
    }

    protected static MissingZoneType getInstance(){
        return instance;
    }
}
