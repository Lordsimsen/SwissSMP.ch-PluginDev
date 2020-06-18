package ch.swisssmp.zones;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

public class ZoneTypes {
    private static Set<ZoneType> types = new HashSet<>();

    protected static Collection<ZoneType> getAll(){
        return types;
    }

    protected static Optional<ZoneType> getType(NamespacedKey key){
        return types.stream().filter(t->t.getKey().equals(key)).findAny();
    }

    protected static void register(ZoneType type){
        ZoneType existing = getType(type.getKey()).orElse(null);
        if(existing!=null){
            Bukkit.getLogger().warning(ZonesPlugin.getPrefix()+" ZoneType "+type.getKey()+" already registered!");
            return;
        }

        types.add(type);
        ZoneContainers.loadAll(type);
    }

    protected static void unregister(ZoneType type){
        types.remove(type);
        ZoneContainers.unloadAll(type);
    }

    protected static void unregister(Plugin plugin){
        for(ZoneType type : types.stream().filter(t->t.getKey().getNamespace().equalsIgnoreCase(plugin.getName())).collect(Collectors.toList())){
            unregister(type);
        }
    }
}
