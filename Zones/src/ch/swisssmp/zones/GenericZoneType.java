package ch.swisssmp.zones;

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
}
