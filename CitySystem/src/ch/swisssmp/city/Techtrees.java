package ch.swisssmp.city;

import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

class Techtrees {
    private static final Set<Techtree> techtrees = new HashSet<>();

    protected static Optional<Techtree> getTechtree(String id){
        return techtrees.stream().filter(t->t.getId().equals(id)).findAny();
    }

    protected static void loadAll(){
        loadAll(null);
    }

    protected static void loadAll(Runnable callback){
        HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), "techtrees.php");
        request.onFinish(()->{
            loadAll(request.getJsonResponse(), callback);
        });
    }

    private static void loadAll(JsonObject json, Runnable callback){
        unloadAll();
        if(json==null || !json.has("techtrees")) return;
        for(JsonElement element : json.getAsJsonArray("techtrees")){
            if(!element.isJsonObject()) continue;
            Techtree techtree = Techtree.load(element.getAsJsonObject()).orElse(null);
            if(techtree==null) continue;
            techtrees.add(techtree);
        }

        if(callback!=null) callback.run();
    }

    protected static void unloadAll(){
        techtrees.clear();
    }
}
