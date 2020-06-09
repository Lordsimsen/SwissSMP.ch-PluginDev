package ch.swisssmp.custompaintings;

import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

public class PaintingDataContainer {
    private final static HashMap<String,PaintingData> paintings = new HashMap<>();

    protected static PaintingData createPainting(String id, int width, int height, int[][] reservedMapIds){
        PaintingData data = new PaintingData(id, width, height, reservedMapIds);
        paintings.put(id, data);
        data.save();
        return data;
    }

    public static Collection<PaintingData> getAll(){
        return paintings.values();
    }

    protected static void loadAll(){
        File paintingsDirectory = getPaintingsDirectory();
        if(!paintingsDirectory.exists()) return;
        for(File file : paintingsDirectory.listFiles((dir, name) -> name.endsWith(".json"))){
            boolean success = load(file);
            if(!success){
                Bukkit.getLogger().info(CustomPaintingsPlugin.getPrefix()+" Invalid painting file: "+file.getName());
            }
        }
    }

    protected static boolean load(File file){
        JsonObject json = JsonUtil.parse(file);
        PaintingData data = PaintingData.load(json);
        if(data==null){
            return false;
        }
        paintings.put(data.getId(),data);
        data.render();
        return true;
    }

    public static void saveAll(){
        for(PaintingData data : paintings.values()){
            data.save();
        }
    }

    protected static void save(PaintingData data){
        File paintingsDirectory = getPaintingsDirectory();
        if(!paintingsDirectory.exists()){
            paintingsDirectory.mkdirs();
        }

        File paintingFile = new File(paintingsDirectory, data.getId()+".json");
        data.save(paintingFile);
    }

    protected static void unloadAll(){
        paintings.clear();
    }

    protected static Optional<PaintingData> getPainting(String paintingId){
        return paintings.containsKey(paintingId) ? Optional.of(paintings.get(paintingId)) : Optional.empty();
    }

    private static File getPaintingsDirectory(){
        return new File(CustomPaintingsPlugin.getInstance().getDataFolder(), "paintings");
    }
}
