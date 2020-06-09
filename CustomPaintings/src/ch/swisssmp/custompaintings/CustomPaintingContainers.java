package ch.swisssmp.custompaintings;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;

public class CustomPaintingContainers {

    private static final HashMap<World,CustomPaintingContainer> containers = new HashMap<>();

    public static CustomPaintingContainer get(World world){
        return containers.get(world);
    }

    protected static CustomPaintingContainer load(World world){
        CustomPaintingContainer container = CustomPaintingContainer.load(world);
        containers.put(world, container);
        return container;
    }

    protected static void unload(World world){
        CustomPaintingContainer container = get(world);
        container.unload();
        containers.remove(world);
    }

    protected static void loadAll(){
        for(World world : Bukkit.getWorlds()){

        }
    }

    protected static void unloadAll(){
        for(CustomPaintingContainer c : containers.values()){
            c.unload();
        }
        containers.clear();
    }
}
