package ch.swisssmp.custompaintings;

import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

public class CustomPaintingContainer {
    private final static HashMap<String, CustomPainting> paintings = new HashMap<>();

    protected static CustomPainting createPainting(String id, String name, int width, int height, int[][] reservedMapIds){
        CustomPainting data = new CustomPainting(id, name, width, height, reservedMapIds);
        paintings.put(id, data);
        data.save();
        return data;
    }

    public static Collection<CustomPainting> getAll(){
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
        CustomPainting data = CustomPainting.load(json);
        if(data==null){
            return false;
        }
        paintings.put(data.getId(),data);
        data.render();
        return true;
    }

    public static void saveAll(){
        for(CustomPainting data : paintings.values()){
            data.save();
        }
    }

    protected static void save(CustomPainting data){
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

    protected static Optional<CustomPainting> getPainting(String paintingId){
        return paintings.containsKey(paintingId) ? Optional.of(paintings.get(paintingId)) : Optional.empty();
    }

    private static File getPaintingsDirectory(){
        return new File(CustomPaintingsPlugin.getInstance().getDataFolder(), "paintings");
    }
}
