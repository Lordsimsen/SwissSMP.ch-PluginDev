package ch.swisssmp.zones;

import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.world.WorldManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

import java.io.File;
import java.util.*;

public class ZoneContainer {

    private final World world;
    private final Set<ZoneCollection> collections = new HashSet<>();

    private ZoneContainer(World world){
        this.world = world;
    }

    public World getBukkitWorld(){
        return world;
    }

    public Optional<ZoneCollection> getCollection(ZoneType type){
        return getCollection(type.getKey());
    }

    public Optional<ZoneCollection> getCollection(NamespacedKey key){
        return collections.stream().filter(c->c.getZoneType().getKey().equals(key)).findAny();
    }

    protected void unload(){
        for(ZoneCollection collection : collections){
            collection.unload();
        }

        collections.clear();
    }

    protected void loadCollection(ZoneType type){
        ZoneCollection collection = ZoneCollection.load(this, type);
        if(collection==null) return;
        collections.add(collection);
    }

    protected void unloadCollection(ZoneType type){
        ZoneCollection collection = getCollection(type).orElse(null);
        if(collection==null) return;
        collection.unload();
        collections.remove(collection);
    }

    protected static ZoneContainer load(World world){
        ZoneContainer result = new ZoneContainer(world);
        for(ZoneType type : ZoneTypes.getAll()){
            result.loadCollection(type);
        }

        return result;
    }

    public static ZoneContainer get(World world){
        return ZoneContainers.get(world);
    }

    private static File getFile(World world){
        return new File(WorldManager.getPluginDirectory(ZonesPlugin.getInstance(), world), "zones.json");
    }
}
