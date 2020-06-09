package ch.swisssmp.custompaintings;

import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.world.WorldManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class CustomPaintingContainer {

    private final World world;
    private final Collection<CustomPainting> paintings = new ArrayList<>();

    private CustomPaintingContainer(World world){
        this.world = world;
    }

    public World getWorld(){
        return world;
    }

    public CustomPainting create(String paintingId, Block origin, BlockFace right, BlockFace up){
        PaintingData paintingData = PaintingData.get(paintingId).orElse(null);
        if(paintingData==null) return null;
        CustomPainting painting = new CustomPainting(UUID.randomUUID(), paintingId, origin, right, up, paintingData.getWidth(), paintingData.getHeight());
        paintings.add(painting);
        return painting;
    }

    public void save(){
        JsonObject json = new JsonObject();
        JsonArray paintingsArray = new JsonArray();
        for(CustomPainting p : paintings){
            paintingsArray.add(p.save());
        }
        json.add("paintings", paintingsArray);
        File file = getPaintingsFile(world);
        JsonUtil.save(file, json);
    }

    protected void unload(){

    }

    protected static CustomPaintingContainer load(World world){
        CustomPaintingContainer result = new CustomPaintingContainer(world);
        File file = getPaintingsFile(world);
        if(!file.exists()) return result;
        JsonObject json = JsonUtil.parse(file);
        if(json==null || !json.has("paintings")) return result;
        for(JsonElement element : json.getAsJsonArray("paintings")){
            if(!element.isJsonObject()) continue;
            CustomPainting painting = CustomPainting.load(world, element.getAsJsonObject());
            if(painting==null) continue;
            result.paintings.add(painting);
        }
        return result;
    }

    private static File getPaintingsFile(World world){
        return new File(WorldManager.getPluginDirectory(CustomPaintingsPlugin.getInstance(), world), "paintings.json");
    }
}
