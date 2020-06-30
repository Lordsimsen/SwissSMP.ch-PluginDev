package ch.swisssmp.zones;

import ch.swisssmp.utils.ItemUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Optional;
import java.util.UUID;

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

    public static Optional<Zone> getZone(ItemStack itemStack){
        String zoneIdString = ItemUtil.getString(itemStack, Zone.ID_PROPERTY);
        if(zoneIdString==null) return Optional.empty();
        UUID zoneUid;
        try{
            zoneUid = UUID.fromString(zoneIdString);
        }
        catch(Exception e){
            return Optional.empty();
        }

        return Zones.findZone(zoneUid);
    }

    public static Optional<Zone> findZone(UUID zoneUid){
        return ZoneContainers.findZone(zoneUid);
    }
}
