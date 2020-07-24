package ch.swisssmp.zones;

import ch.swisssmp.zones.editor.ZoneEditors;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class ZonesPlugin extends JavaPlugin {
    private static ZonesPlugin plugin;

    @Override
    public void onEnable(){
        plugin = this;

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        Bukkit.getPluginCommand("zones").setExecutor(new ZonesCommand());
        Bukkit.getPluginCommand("zone").setExecutor(new ZoneCommand());

        ZoneType cuboidZoneType = new GenericZoneType(this, "generic_cuboid", "Quader-Zone", "WORLD_GUARD_CUBOID_ZONE", RegionType.CUBOID);
        ZoneType polygonZoneType = new GenericZoneType(this, "generic_polygon", "Polygon-Zone", "WORLD_GUARD_POLYGON_ZONE", RegionType.POLYGON);
        ZoneType missingZoneType = MissingZoneType.initialize(this);
        Zones.setGenericCuboidZoneType(cuboidZoneType);
        Zones.setGenericPolygonZoneType(polygonZoneType);
        Zones.registerZoneType(cuboidZoneType);
        Zones.registerZoneType(polygonZoneType);
        Zones.registerZoneType(missingZoneType);
        ZoneContainers.loadAll();

        Zones.updateTokens();

        Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable(){
        ZoneContainers.unloadAll();
        Zones.unregisterZoneTypes(this);
        HandlerList.unregisterAll(this);
        ZoneEditors.cancelAll();
        Bukkit.getScheduler().cancelTasks(this);

        Bukkit.getLogger().info(getDescription().getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
    }

    public static ZonesPlugin getInstance(){
        return plugin;
    }

    public static String getPrefix(){
        return "[Zones]";
    }
}
