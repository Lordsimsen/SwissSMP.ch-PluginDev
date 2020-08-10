package ch.swisssmp.city;

import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class Citizenships {
    private static final Set<Citizenship> citizenships = new HashSet<>();

    protected static Optional<Citizenship> getCitizenship(UUID cityId, String name){
        return citizenships.stream().filter(c->c.getCityId().equals(cityId) && c.getName().equalsIgnoreCase(name)).findAny();
    }

    protected static Optional<Citizenship> getCitizenship(UUID cityId, UUID playerUid){
        return citizenships.stream().filter(c->c.getCityId().equals(cityId) && c.getUniqueId().equals(playerUid)).findAny();
    }

    protected static Collection<Citizenship> getPlayerCitizenships(UUID playerUid){
        return citizenships.stream().filter(c->c.getUniqueId().equals(playerUid)).collect(Collectors.toList());
    }

    protected static Collection<Citizenship> getAllCitizenships(UUID cityId){
        return citizenships.stream().filter(c->c.getCityId().equals(cityId)).collect(Collectors.toList());
    }

    protected static int getCitizenCount(UUID cityId){return (int) citizenships.stream().filter(c->c.getCityId().equals(cityId)).count();}

    protected static void add(Citizenship citizenship){
        citizenships.remove(citizenship);
    }

    protected static void remove(Citizenship citizenship){
        citizenships.remove(citizenship);
    }

    protected static void loadAll(){
        loadAll((success)->{
            if(success){
                Bukkit.getLogger().info(CitySystemPlugin.getPrefix()+" Alle Bürgerschaften geladen.");
            }
            else{
                Bukkit.getLogger().info(CitySystemPlugin.getPrefix()+" Bürgerschaften konnten nicht neu geladen werden.");
            }
        });
    }

    protected static void loadAll(Consumer<Boolean> callback){
        HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), CitySystemUrl.GET_CITIZENSHIPS);

        request.onFinish(()->{
            JsonObject json = request.getJsonResponse();
            boolean success = json!=null && JsonUtil.getBool("success", json);
            String message = json!=null ? JsonUtil.getString("message", json) : null;
            if(message!=null){
                Bukkit.getLogger().info(CitySystemPlugin.getPrefix()+" "+message);
            }
            if(success){
                loadAll(json.getAsJsonArray("citizenships"));
            }
            if(callback!=null) callback.accept(success);
        });
    }

    private static void loadAll(JsonArray citizenshipsArray){
        citizenships.clear();
        Collection<Citizenship> citizenships = new Stack<>();
        for(JsonElement element : citizenshipsArray){
            if(!element.isJsonObject()) continue;
            Citizenship citizenship = Citizenship.get(element.getAsJsonObject()).orElse(null);
            if(citizenship==null){
                Bukkit.getLogger().warning(CitySystemPlugin.getPrefix()+" Konnte Bürgerschaft nicht laden:\n"+element.toString());
                continue;
            }
            citizenships.add(citizenship);
        }

        Citizenships.citizenships.addAll(citizenships);
    }

    protected static void unloadAll(){
        citizenships.clear();
    }
}
