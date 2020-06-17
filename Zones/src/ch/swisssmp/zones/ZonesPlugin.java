package ch.swisssmp.zones;

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

        ZoneType cuboidZoneType = new GenericZoneType(this, "generic_cuboid", "Generische Quader-Zone", "WORLD_GUARD_CUBOID_ZONE", RegionType.CUBOID);
        ZoneType polygonZoneType = new GenericZoneType(this, "generic_polygon", "Generische Polygon-Zone", "WORLD_GUARD_POLYGON_ZONE", RegionType.POLYGON);
        Zones.setGenericCuboidZoneType(cuboidZoneType);
        Zones.setGenericPolygonZoneType(polygonZoneType);
        Zones.registerZoneType(cuboidZoneType);
        Zones.registerZoneType(polygonZoneType);
        ZoneContainers.loadAll();

        Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable(){
        ZoneContainers.unloadAll();
        Zones.unregisterZoneTypes(this);
        HandlerList.unregisterAll(this);
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
