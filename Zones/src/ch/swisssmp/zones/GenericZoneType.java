package ch.swisssmp.zones;

import ch.swisssmp.zones.editor.selection.DefaultCuboidSelector;
import ch.swisssmp.zones.editor.selection.DefaultPolygonSelector;
import ch.swisssmp.zones.editor.selection.PointSelector;
import ch.swisssmp.zones.editor.visualization.VisualizationColorScheme;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class GenericZoneType extends ZoneType {

    private final RegionType regionType;
    private final String customEnum;

    protected GenericZoneType(Plugin plugin, String id, String name, String customEnum, RegionType type) {
        super(plugin, id, name);
        this.regionType = type;
        this.customEnum = customEnum;
    }

    @Override
    public String getCustomEnum() {
        return customEnum;
    }

    @Override
    public String getDisplayName(String name) {
        return ChatColor.AQUA+name;
    }

    @Override
    public List<String> getItemLore(Zone zone) {
        return getDefaultLore(zone);
    }

    @Override
    public RegionType getRegionType() {
        return regionType;
    }

    @Override
    public PointSelector createSelector(Zone zone) {
        switch (regionType){
            case CUBOID:return new DefaultCuboidSelector((CuboidZone) zone);
            case POLYGON:return new DefaultPolygonSelector((PolygonZone) zone);
            default:{
                Bukkit.getLogger().warning(ZonesPlugin.getPrefix()+" GenericZoneType can't create a PointSelector for RegionType "+regionType+"!");
                return null;
            }
        }
    }

    @Override
    public VisualizationColorScheme getVisualizationColorScheme() {
        return getDefaultVisualizationColorScheme();
    }
}
