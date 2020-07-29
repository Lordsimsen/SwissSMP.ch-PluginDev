package ch.swisssmp.city;

import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Collectors;

public class Addons {
    private static final Set<Addon> addons = new HashSet<>();

    protected static Collection<Addon> getAll(UUID cityId){
        return addons.stream().filter(a->a.getCityId().equals(cityId)).collect(Collectors.toSet());
    }

    protected static Collection<Addon> getAll(City city){
        return getAll(city.getUniqueId());
    }

    protected static Optional<Addon> getAddon(UUID cityId, String addonId){
        return addons.stream().filter(a->a.getCityId().equals(cityId) && a.getAddonId().equals(addonId)).findAny();
    }

    protected static void add(Addon addon){
        addons.add(addon);
    }

    protected static void remove(Addon addon){
        addons.remove(addon);
    }

    protected static void loadAll(){
        loadAll((Runnable) null);
    }

    protected static void loadAll(Runnable callback){
        HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), CitySystemUrl.GET_ADDONS);
        request.onFinish(()->{
            JsonObject json = request.getJsonResponse();
            loadAll(json);
            if(callback!=null) callback.run();
        });
    }

    private static void loadAll(JsonObject json){
        addons.clear();
        if(json==null || !json.has("addons")) return;
        Set<Addon> addons = new HashSet<Addon>();
        for(JsonElement element : json.getAsJsonArray("addons")){
            if(!element.isJsonObject()) continue;
            Addon addon = Addon.load(element.getAsJsonObject()).orElse(null);
            if(addon==null){
                Bukkit.getLogger().warning(CitySystemPlugin.getPrefix()+" Konnte Addon nicht laden:\n"+element.toString());
                continue;
            }
            addons.add(addon);
        }

        Addons.addons.addAll(addons);
    }

    protected static void unloadAll(){
        addons.clear();
    }
}
