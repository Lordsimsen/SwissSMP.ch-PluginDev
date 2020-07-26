package ch.swisssmp.city;

import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Techtree {
    private final String id;
    private final String name;
    private final String addonDetailUrl;
    private final List<CityLevel> levels;

    private Techtree(String id, String name, String addonDetailUrl, List<CityLevel> levels){
        this.id = id;
        this.name = name;
        this.addonDetailUrl = addonDetailUrl;
        this.levels = new ArrayList<>(levels);
    }

    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getAddonDetailUrl(){
        return addonDetailUrl;
    }

    public List<CityLevel> getLevels(){
        return Collections.unmodifiableList(levels);
    }

    protected static Optional<Techtree> load(JsonObject json){
        if(json==null) return Optional.empty();
        String id = JsonUtil.getString("techtree_id", json);
        String name = JsonUtil.getString("name", json);
        if(id==null || name==null) return Optional.empty();
        String addonDetailUrl = JsonUtil.getString("addon_detail_url", json);
        List<CityLevel> levels = new ArrayList<>();
        if(json.has("levels")){
            for(JsonElement element : json.getAsJsonArray("levels")){

            }
        }
        return Optional.of(new Techtree(id, name, addonDetailUrl, levels));
    }
}
