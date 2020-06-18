package ch.swisssmp.zones;

import org.bukkit.plugin.Plugin;

public class Zones {

    private static ZoneType genericCuboidZoneType;
    private static ZoneType genericPolygonZoneType;

    protected static void setGenericCuboidZoneType(ZoneType type){
        genericCuboidZoneType = type;
    }

    protected static void setGenericPolygonZoneType(ZoneType type){
        genericPolygonZoneType = type;
    }

    public static ZoneType getGenericCuboidZoneType(){
        return genericCuboidZoneType;
    }

    public static ZoneType getGenericPolygonZoneType(){
        return genericPolygonZoneType;
    }

    public static void registerZoneType(ZoneType type){
        ZoneTypes.register(type);
    }

    public static void unregisterZoneType(ZoneType type){
        ZoneTypes.unregister(type);
    }

    public static void unregisterZoneTypes(Plugin plugin){
        ZoneTypes.unregister(plugin);
    }
}
