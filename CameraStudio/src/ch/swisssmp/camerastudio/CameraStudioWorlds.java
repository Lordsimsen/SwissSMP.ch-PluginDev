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

    protected static void loadAll(){
        for(World world : Bukkit.getWorlds()){
            CameraStudioWorlds.load(world);
        }
    }

    protected static void unload(World world){
        CameraStudioWorld studioWorld = getWorld(world);
        if(studioWorld==null) return;
        studioWorld.unload();
        worlds.remove(world);
    }

    protected static void unloadAll(){
        for(CameraStudioWorld world : worlds.values()){
            world.unload();
        }

        worlds.clear();
    }

    public static Optional<CameraPathElement> getElement(UUID elementUid){

        CameraPath cameraPath = CameraStudio.inst().getPath(elementUid).orElse(null);
        if(cameraPath!=null){
            return Optional.of(cameraPath);
        }

        CameraPathSequence sequence = CameraStudio.inst().getSequence(elementUid).orElse(null);
        if(sequence!=null){
            return Optional.of(sequence);
        }

        return Optional.empty();
    }

    public static Optional<CameraPath> getPath(UUID pathUid){
        return worlds.values().stream().filter(w->w.hasPath(pathUid)).map(w->w.getPath(pathUid).orElse(null)).findAny();
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
