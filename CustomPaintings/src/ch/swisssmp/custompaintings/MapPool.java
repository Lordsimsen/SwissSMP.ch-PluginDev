package ch.swisssmp.custompaintings;

import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.map.MapView;

import java.io.File;
import java.util.Collection;
import java.util.Stack;

public class MapPool {
    private static final Stack<Integer> unusedIds = new Stack<>();

    protected static void unlinkMap(MapView view){
        unusedIds.add(view.getId());
        save();
    }

    protected static void unlinkMaps(Collection<MapView> views){
        for(MapView view : views){
            unusedIds.add(view.getId());
        }
        save();
    }

    protected static MapView createMap(){
        while(unusedIds.size()>0){
            try{
                int id = unusedIds.pop();
                MapView view = Bukkit.getMap(id);
                if(view!=null) return view;
            }
            catch(Exception e){
                e.printStackTrace();
            }

            save();
        }

        return Bukkit.createMap(Bukkit.getWorlds().get(0));
    }

    protected static void save(){
        File file = getSaveFile();
        JsonObject json =  new JsonObject();
        JsonArray idsArray = new JsonArray();
        for(int id : unusedIds){
            idsArray.add(id);
        }
        json.add("unused_ids", idsArray);
        JsonUtil.save(file, json);
    }

    protected static void load(){
        unusedIds.clear();
        File file = getSaveFile();
        if(!file.exists()) return;
        JsonObject json = JsonUtil.parse(file);
        if(json==null) return;
        JsonArray idsArray = json.has("unused_ids") ? json.getAsJsonArray("unused_ids") : null;
        if(idsArray==null) return;
        for(JsonElement element : idsArray){
            if(!element.isJsonPrimitive()) continue;
            unusedIds.add(element.getAsInt());
        }
    }

    private static File getSaveFile(){
        return new File(CustomPaintingsPlugin.getInstance().getDataFolder(), "map_ids.json");
    }
}
