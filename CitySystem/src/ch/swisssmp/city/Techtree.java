package ch.swisssmp.city;

import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

public class Techtree {
    private final String id;
    private final String name;
    private final String addonDetailUrl;
    private final List<CityLevel> levels;
    private final Set<AddonType> addonTypes;

    private Techtree(String id, String name, String addonDetailUrl, List<CityLevel> levels, Set<AddonType> addonTypes){
        this.id = id;
        this.name = name;
        this.addonDetailUrl = addonDetailUrl;
        this.levels = new ArrayList<>(levels);
        this.addonTypes = new HashSet<>(addonTypes);
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

    public CityLevel getLevel(int index){
        return levels.get(index);
    }

    public Optional<CityLevel> getLevel(String id){
        return levels.stream().filter(l->l.getId().equalsIgnoreCase(id)).findAny();
    }

    public int getLevelIndex(String levelId){
        CityLevel level = getLevel(levelId).orElse(null);
        return level!=null ? getLevelIndex(level) : -1;
    }

    public int getLevelIndex(CityLevel level){
        return levels.indexOf(level);
    }

    public Set<AddonType> getAddonTypes(){return Collections.unmodifiableSet(addonTypes);}

    public Optional<AddonType> getAddonType(String key){
        return addonTypes.stream().filter(a->a.getAddonId().equalsIgnoreCase(key) || a.getName().equalsIgnoreCase(key)).findAny();
    }

    public Optional<AddonType> findAddonType(String key){
        Optional<AddonType> exactResult = getAddonType(key);
        if(exactResult.isPresent()) return exactResult;
        for(AddonType addonType : addonTypes){
            for(String synonym : addonType.getSynonyms()){
                if(synonym.toLowerCase().contains(key.toLowerCase())) return Optional.of(addonType);
            }
        }
        return Optional.empty();
    }

    public void updateAddonState(Addon addon){
        if(addon.getState()==AddonState.ACCEPTED || addon.getState()==AddonState.ACTIVATED || addon.getState()==AddonState.BLOCKED) return;
        City city = addon.getCity();
        int currentLevel = levels.indexOf(city.getLevel());
        AddonType type = addon.getType();
        int requiredLevel = type.getCityLevel();
        if(currentLevel<requiredLevel){
            addon.setAddonState(AddonState.UNAVAILABLE, AddonStateReason.CITY_LEVEL);
            return;
        }

        String[] requiredAddons = type.getRequiredAddons();
        boolean requiredAddonsPresent = true;
        for(String requiredAddonId : requiredAddons){
            Addon requiredAddon = city.getAddon(requiredAddonId).orElse(null);
            if(requiredAddon!=null && (requiredAddon.getState()==AddonState.ACCEPTED || requiredAddon.getState()==AddonState.ACTIVATED)){
                continue;
            }

            requiredAddonsPresent = false;
            break;
        }

        if(!requiredAddonsPresent){
            addon.setAddonState(AddonState.UNAVAILABLE, AddonStateReason.REQUIRED_ADDONS);
            return;
        }

        addon.setAddonState(AddonState.AVAILABLE);
    }

    public void loadIcons(){
        for(AddonType addonType : addonTypes){
            LivemapInterface.updateAddonIcon(addonType);
        }
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
                if(!element.isJsonObject()) continue;
                CityLevel level = CityLevel.load(element.getAsJsonObject()).orElse(null);
                if(level==null) continue;
                levels.add(level);
            }
        }
        Set<AddonType> addons = new HashSet<>();
        JsonArray addonsSection = json.has("addons") ? json.getAsJsonArray("addons") : null;
        if(addonsSection!=null){
            for(JsonElement element : addonsSection){
                if(!element.isJsonObject()) continue;
                JsonObject addonSection = element.getAsJsonObject();
                AddonType addonType = AddonType.load(addonSection).orElse(null);
                if(addonType==null) continue;
                addons.add(addonType);
            }
        }
        return Optional.of(new Techtree(id, name, addonDetailUrl, levels, addons));
    }
}
