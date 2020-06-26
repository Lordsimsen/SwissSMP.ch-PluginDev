package ch.swisssmp.netherportals;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;

public class WorldConfigurations {
    private static final Set<WorldConfiguration> configurations = new HashSet<>();

    protected static WorldConfiguration get(World world){
        return configurations.stream().filter(c->c.getBukkitWorld()==world).findAny().orElse(null);
    }

    protected static WorldConfiguration load(World world){
        WorldConfiguration existing = get(world);
        if(existing!=null) return existing;
        WorldConfiguration configuration = WorldConfiguration.load(world);
        configurations.add(configuration);
        return configuration;
    }

    protected static void unload(World world){
        WorldConfiguration configuration = get(world);
        if(configuration==null) return;
        configuration.unload();
        configurations.remove(configuration);
    }

    protected static void loadAll(){
        for(World world : Bukkit.getWorlds()){
            load(world);
        }
    }

    protected static void unloadAll(){
        for(WorldConfiguration configuration : configurations){
            configuration.unload();
        }

        configurations.clear();
    }
}
