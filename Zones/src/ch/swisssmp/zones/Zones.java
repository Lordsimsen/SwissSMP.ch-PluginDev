package ch.swisssmp.zones;

import ch.swisssmp.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
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

    public static void updateTokens(){
        for(Player player : Bukkit.getOnlinePlayers()){
            updateTokens(player);
        }
    }

    public static void updateTokens(Player player){
        updateTokens(player.getInventory());
        if(player.getOpenInventory()!=null) updateTokens(player.getOpenInventory().getTopInventory());
    }

    public static void updateTokens(Inventory inventory){

        for(ItemStack itemStack : inventory){
            if(itemStack==null) continue;
            Zone zone = Zone.get(itemStack).orElse(null);
            if(zone==null) continue;
            zone.updateTokenStack(itemStack);
        }
    }

    public static void updateTokens(Zone zone){
        for(Player player : Bukkit.getOnlinePlayers()){
            updateTokens(zone, player);
        }
    }

    private static void updateTokens(Zone zone, Player player){
        updateTokens(zone, player.getInventory());
        if(player.getOpenInventory()!=null) updateTokens(zone, player.getOpenInventory().getTopInventory());
    }

    private static void updateTokens(Zone zone, Inventory inventory){
        for(ItemStack itemStack : inventory){
            if(itemStack==null) continue;
            Zone onStack = Zone.get(itemStack).orElse(null);
            if(onStack!=zone) continue;
            zone.updateTokenStack(itemStack);
        }
    }
}
