package ch.swisssmp.zones;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class ZoneContainers {
    private static final HashMap<World,ZoneContainer> containers = new HashMap<>();

    protected static ZoneContainer load(World world){
        ZoneContainer container = ZoneContainer.load(world);
        containers.put(world,container);
        return container;
    }

    protected static void unload(World world){
        ZoneContainer container = get(world);
        container.unload();
        containers.remove(world);
    }

    protected static ZoneContainer get(World world){
        return containers.get(world);
    }

    protected static void loadAll(){
        for(World world : Bukkit.getWorlds()){
            load(world);
        }
    }

    protected static void loadAll(ZoneType type){
        for(ZoneContainer container : containers.values()){
            container.loadCollection(type);
        }
    }

    protected static void unloadAll(){
        for(ZoneContainer container : containers.values()){
            container.unload();
        }

        containers.clear();
    }

    protected static void unloadAll(ZoneType type){
        for(ZoneContainer container : containers.values()){
            container.unloadCollection(type);
        }
    }

    protected static Optional<Zone> findZone(UUID zoneUid){
        for(ZoneContainer c : containers.values()){
            Optional<Zone> result = c.findZone(zoneUid);
            if(result.isPresent()) return result;
        }
        
        return Optional.of(new MissingZone(zoneUid));
    }
}
