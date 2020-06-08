package ch.swisssmp.camerastudio;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.*;
import java.util.stream.Collectors;

public class CameraStudioWorlds {
    private final static HashMap<World,CameraStudioWorld> worlds = new HashMap<>();

    protected static CameraStudioWorld load(World world){
        CameraStudioWorld studioWorld = CameraStudioWorld.load(world);
        worlds.put(world, studioWorld);
        return studioWorld;
    }

    protected static void unload(World world){
        CameraStudioWorld studioWorld = getWorld(world);
        if(studioWorld==null) return;
        studioWorld.unload();
        worlds.remove(world);
    }

    public static Optional<CameraPath> getPath(UUID pathUid){
        Bukkit.getLogger().info("Trying to find the path "+pathUid);
        Optional<CameraPath> result = worlds.values().stream().filter(w->w.hasPath(pathUid)).map(w->w.getPath(pathUid).orElse(null)).findAny();
        Bukkit.getLogger().info(result.isPresent()?"Found it":"Not found");
        return result;
    }

    public static Collection<CameraPath> getAllPaths(){
        return worlds.values().stream().map(CameraStudioWorld::getAllPaths).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static Optional<CameraPathSequence> getSequence(UUID sequenceUid){
        return worlds.values().stream().filter(w->w.hasSequence(sequenceUid)).map(w->w.getSequence(sequenceUid).orElse(null)).findAny();
    }

    public static Collection<CameraPathSequence> getAllSequences(){
        return worlds.values().stream().map(CameraStudioWorld::getAllPathSequences).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static CameraStudioWorld getWorld(World world){
        return worlds.getOrDefault(world, null);
    }
}
