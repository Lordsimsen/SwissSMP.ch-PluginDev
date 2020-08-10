package ch.swisssmp.city;

import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Techtree {
    private final String id;
    private final String namespace;
    private String name;
    private List<String> description;
    private String addonDetailUrl;
    private List<CityLevel> levels;
    private Set<AddonType> addonTypes;

    private Techtree(String id){
        this.id = id;
        this.namespace = id.toLowerCase();
    }

    public String getId(){
        return id;
    }

    public String getNamespace(){return namespace;}

    public String getName(){
        return name;
    }

    public List<String> getDescription(){
        return description;
    }

    public String getAddonDetailUrl(){
        return addonDetailUrl;
    }

    public List<CityLevel> getLevels(){
        return Collections.unmodifiableList(levels);
    }

    public CityLevel getLevel(int index){
        return index>=0 && index<levels.size() ? levels.get(index) : null;
    }

    public Optional<CityLevel> getLevel(String id){
        return levels.stream().filter(l->l.getId().equalsIgnoreCase(id)).findAny();
    }

    public boolean hasLevel(String levelId){
        return getLevel(levelId).isPresent();
    }

    public int getLevelIndex(String levelId){
        CityLevel level = getLevel(levelId).orElse(null);
        return level!=null ? getLevelIndex(level) : -1;
    }

    public int getLevelIndex(CityLevel level){
        return levels.indexOf(level);
    }

    public Set<AddonType> getAddonTypes(){return Collections.unmodifiableSet(addonTypes);}

    public Set<AddonType> getAddonTypes(CityLevel level){
        int index = this.levels.indexOf(level);
        if(index<0) return Collections.emptySet();
        return addonTypes.stream().filter(t->t.getCityLevel()==index).collect(Collectors.toSet());}

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
        AddonType type = addon.getType(this);
        if(type==null){
            Bukkit.getLogger().warning(CitySystemPlugin.getPrefix()+" AddonType "+addon.getAddonId()+" fehlt!");
            return;
        }
        int requiredLevelIndex = type!=null ? type.getCityLevel() : 0;
        CityLevel requiredLevel = this.getLevel(requiredLevelIndex);
        if(requiredLevel==null){
            addon.setAddonState(AddonState.UNAVAILABLE, AddonStateReason.NONE);
            return;
        }
        if(!city.hasLevel(requiredLevel)){
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

    public LevelStateInfo getLevelState(CityLevel level, City city){
        if(level==null){
            Bukkit.getLogger().warning("CityLevel is null!");
            return null;
        }
        if(city==null){
            Bukkit.getLogger().warning("City is null!");
            return null;
        }
        if(city.hasLevel(level)){
            return new LevelStateInfo(LevelState.UNLOCKED);
        }

        List<String> conditions = new ArrayList<>();
        boolean overallSuccess = true;

        int levelIndex = this.getLevelIndex(level);
        CityLevel previous = levelIndex>0 && levelIndex-1<this.levels.size() ? this.getLevel(levelIndex-1) : null;
        if(previous!=null){
            boolean success = city.hasLevel(previous);
            conditions.add((success ? ChatColor.GREEN: ChatColor.RED)+"- "+previous.getName());
            overallSuccess &= success;
        }

        int minPopulation = level.getMinPopulation();
        if(minPopulation>0){
            boolean success = city.getCitizenCount()>=minPopulation;
            conditions.add((success ? ChatColor.GREEN: ChatColor.RED)+"- Min. "+minPopulation+" Bürger");
            overallSuccess &= success;
        }
        int minAddonCount = level.getMinAddonCount();
        if (minAddonCount > 0) {
            boolean success = city.getAddons(previous).stream().filter(a->a.getState()==AddonState.ACCEPTED || a.getState()==AddonState.ACTIVATED).count()>=minAddonCount;
            conditions.add((success ? ChatColor.GREEN: ChatColor.RED)+"- Min. "+minAddonCount+" Addons der Stufe");
            conditions.add((success ? ChatColor.GREEN: ChatColor.RED)+"  "+previous.getName());
            overallSuccess &= success;
        }

        String[] requiredAddons = level.getRequiredAddons();
        if(requiredAddons.length>0){
            for(String requiredAddonId : requiredAddons){
                Addon addon = city.getAddon(requiredAddonId).orElse(null);
                boolean success = addon.getState()==AddonState.ACCEPTED || addon.getState()==AddonState.ACTIVATED;
                conditions.add((success ? ChatColor.GREEN: ChatColor.RED)+"- "+addon.getName());
                overallSuccess &= success;
            }
        }

        if(overallSuccess){
            return new LevelStateInfo(LevelState.AVAILABLE);
        }

        conditions.add(0, ChatColor.GRAY+"Deine Stadt benötigt:");
        return new LevelStateInfo(LevelState.UNAVAILABLE, conditions);
    }

    public void loadIcons(){
        for(AddonType addonType : addonTypes){
            LivemapInterface.updateAddonIcon(addonType);
        }
    }

    public void reload(){
        reload(null);
    }

    public void reload(Consumer<Boolean> callback){
        HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), CitySystemUrl.GET_TECHTREE, new String[]{
                "techtree_id="+ URLEncoder.encode(id)
        });
        request.onFinish(()->{
            JsonObject json = request.getJsonResponse();
            boolean success = json!=null && JsonUtil.getBool("success", json);
            String message = json!=null ? JsonUtil.getString("message", json) : null;
            if(message!=null){
                Bukkit.getLogger().info(CitySystemPlugin.getPrefix()+" "+message);
            }
            if(success){
                loadData(json.getAsJsonObject("techtree"));
            }
            if(callback!=null) callback.accept(success);
        });
    }

    private void loadData(JsonObject json){
        this.name = JsonUtil.getString("name", json);
        this.description = JsonUtil.getStringList("description", json);
        this.addonDetailUrl = JsonUtil.getString("addon_detail_url", json);
        List<CityLevel> levels = new ArrayList<>();
        if(json.has("levels")){
            for(JsonElement element : json.getAsJsonArray("levels")){
                if(!element.isJsonObject()) continue;
                CityLevel level = CityLevel.load(this, element.getAsJsonObject()).orElse(null);
                if(level==null) continue;
                levels.add(level);
            }
        }
        this.levels = levels;
        Set<AddonType> addons = new HashSet<>();
        JsonArray addonsSection = json.has("addons") ? json.getAsJsonArray("addons") : null;
        if(addonsSection!=null){
            for(JsonElement element : addonsSection){
                if(!element.isJsonObject()) continue;
                JsonObject addonSection = element.getAsJsonObject();
                AddonType addonType = AddonType.load(this, addonSection).orElse(null);
                if(addonType==null){
                    Bukkit.getLogger().warning(CitySystemPlugin.getPrefix()+" Konnte AddonType vom Techtree "+id+" nicht laden:\n"+element.toString());
                    continue;
                }
                addons.add(addonType);
            }
        }
        this.addonTypes = addons;

        loadIcons();
    }

    protected static Optional<Techtree> load(JsonObject json){
        if(json==null) return Optional.empty();
        String id = JsonUtil.getString("techtree_id", json);
        if(id==null) return Optional.empty();
        Techtree techtree = new Techtree(id);
        techtree.loadData(json);
        return Optional.of(techtree);
    }
}
