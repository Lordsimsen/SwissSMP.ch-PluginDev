package ch.swisssmp.zones;

import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.world.WorldManager;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ZoneCollection {

    private final ZoneContainer container;
    private final ZoneType type;
    private final Set<Zone> zones = new HashSet<>();

    private ZoneCollection(ZoneContainer container, ZoneType type){
        this.container = container;
        this.type = type;
    }

    public ZoneContainer getContainer(){
        return container;
    }

    public ZoneType getZoneType(){return type;}

    public Collection<Zone> getAllZones(){
        return zones;
    }

    public Zone createZone(String name){
        Zone zone = type.createZone(name);
        zones.add(zone);
        return zone;
    }

    public void unload(){
        for(Zone zone : zones){
            zone.unload();
        }

        zones.clear();
    }

    protected File getDirectory(){
        NamespacedKey key = this.type.getKey();
        File pluginDirectory = WorldManager.getPluginDirectory(ZonesPlugin.getInstance(), container.getBukkitWorld());
        return new File(pluginDirectory, key.getNamespace()+"/"+key.getKey());
    }

    protected static ZoneCollection load(ZoneContainer container, ZoneType type){
        ZoneCollection collection = new ZoneCollection(container, type);
        File directory = collection.getDirectory();
        if(directory.exists()){
            for(File file : directory.listFiles((f)->f.getName().endsWith(".json"))){
                JsonObject json = JsonUtil.parse(file);
                if(json==null) continue;
                Zone zone = Zone.load(collection, json).orElse(null);
                if(zone==null) continue;
                collection.zones.add(zone);
            }
        }

        return collection;
    }
}
