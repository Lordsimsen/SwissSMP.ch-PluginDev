package ch.swisssmp.city;

import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.function.Consumer;

class Techtrees {
    private static final Set<Techtree> techtrees = new HashSet<>();

    protected static Optional<Techtree> getTechtree(String id){
        return techtrees.stream().filter(t->t.getId().equals(id)).findAny();
    }

    protected static void loadAll(){
        loadAll(null);
    }

    protected static void loadAll(Consumer<Boolean> callback){
        HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), CitySystemUrl.GET_TECHTREES);
        request.onFinish(()->{
            loadAll(request.getJsonResponse(), callback);
        });
    }

    private static void loadAll(JsonObject json, Consumer<Boolean> callback){
        unloadAll();
        boolean success = json != null && JsonUtil.getBool("success", json);
        if(success){
            for(JsonElement element : json.getAsJsonArray("techtrees")){
                if(!element.isJsonObject()) continue;
                Techtree techtree = Techtree.load(element.getAsJsonObject()).orElse(null);
                if(techtree==null){
                    Bukkit.getLogger().warning(CitySystemPlugin.getPrefix()+" Konnte Techtree nicht laden:\n"+element.toString());
                    continue;
                }
                techtrees.add(techtree);
            }
        }

        if(callback!=null) callback.accept(success);
    }

    protected static void unloadAll(){
        techtrees.clear();
    }

    protected static Collection<Techtree> getAll(){return Collections.unmodifiableSet(techtrees);}
}
