package ch.swisssmp.transformations;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;

public class TransformationContainers {
    private static final HashMap<World,TransformationContainer> containers = new HashMap<World, TransformationContainer>();

    protected static TransformationContainer getContainer(World world){
        return containers.get(world);
    }

    protected static TransformationContainer load(World world){
        TransformationContainer container = TransformationContainer.load(world);
        containers.put(world, container);
        return container;
    }

    protected static void unload(World world){
        TransformationContainer container = getContainer(world);
        if(container==null) return;
        container.unload();
        containers.remove(world);
    }

    protected static void loadAll(){
        for(World world : Bukkit.getWorlds()){
            TransformationContainer.load(world);
        }
    }

    protected static void unloadAll(){
        for(TransformationContainer container : containers.values()){
            container.unload();
        }

        containers.clear();
    }
}
