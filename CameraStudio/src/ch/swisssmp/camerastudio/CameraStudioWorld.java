package ch.swisssmp.camerastudio;

import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.world.WorldManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.World;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class CameraStudioWorld {

    private final World world;
    private final Collection<CameraPath> paths;
    private final Collection<CameraPathSequence> sequences;

    private CameraStudioWorld(World world){
        this.world = world;
        this.paths = new ArrayList<>();
        this.sequences = new ArrayList<>();
    }

    public World getBukkitWorld(){
        return world;
    }

    public CameraPath createPath(String name){
        CameraPath path = new CameraPath(this, UUID.randomUUID(), name);
        paths.add(path);
        return path;
    }

    public void remove(CameraPath path){
        this.paths.remove(path);
    }

    public CameraPathSequence createSequence(String name){
        CameraPathSequence sequence = new CameraPathSequence(this, UUID.randomUUID(), name);
        sequences.add(sequence);
        return sequence;
    }

    public void remove(CameraPathSequence sequence){
        this.sequences.remove(sequence);
    }

    public boolean hasPath(UUID pathUid){
        return paths.stream().anyMatch(p->p.getUniqueId().equals(pathUid));
    }

    public Optional<CameraPath> getPath(UUID pathUid){
        return paths.stream().filter(p->p.getUniqueId().equals(pathUid)).findAny();
    }

    public Collection<CameraPath> getAllPaths(){
        return paths;
    }

    public Collection<CameraPathSequence> getAllPathSequences(){
        return sequences;
    }

    public boolean hasSequence(UUID sequenceUid){
        return sequences.stream().anyMatch(p->p.getUniqueId().equals(sequenceUid));
    }

    public Optional<CameraPathSequence> getSequence(UUID sequenceUid){
        return sequences.stream().filter(s->s.getUniqueId().equals(sequenceUid)).findAny();
    }

    public void save(){
        JsonObject json = new JsonObject();
        JsonArray pathsArray = new JsonArray();
        JsonArray sequencesArray = new JsonArray();
        for(CameraPath path : paths){
            pathsArray.add(path.save());
        }
        for(CameraPathSequence sequence : sequences){
            sequencesArray.add(sequence.save());
        }

        if(pathsArray.size()>0) json.add("paths", pathsArray);
        if(sequencesArray.size()>0) json.add("sequences", sequencesArray);
        File file = getStudioWorldFile(world);
        JsonUtil.save(file, json);
    }

    public void unload(){

    }

    protected static void save(World world){
        CameraStudioWorld.get(world).save();
    }

    protected static CameraStudioWorld load(World world){
        CameraStudioWorld result = new CameraStudioWorld(world);
        File file = getStudioWorldFile(world);
        if(!file.exists()){
            return result;
        }

        JsonObject json = JsonUtil.parse(file);
        if(json==null){
            return result;
        }

        if(json.has("paths")){
            for(JsonElement element : json.getAsJsonArray("paths")){
                if(!element.isJsonObject()) continue;
                JsonObject pathSection = element.getAsJsonObject();
                CameraPath path = CameraPath.load(result, pathSection);
                if(path==null) continue;
                result.paths.add(path);
            }
        }

        if(json.has("sequences")){
            for(JsonElement element : json.getAsJsonArray("sequences")){
                if(!element.isJsonObject()) continue;
                JsonObject sequenceSection = element.getAsJsonObject();
                CameraPathSequence sequence = CameraPathSequence.load(result, sequenceSection);
                if(sequence==null) continue;
                result.sequences.add(sequence);
            }
        }

        return result;
    }

    public static CameraStudioWorld get(World world){
        return CameraStudioWorlds.getWorld(world);
    }

    private static File getStudioWorldFile(World world){
        return new File(WorldManager.getPluginDirectory(CameraStudioPlugin.getInstance(), world), "studio_world.json");
    }
}
