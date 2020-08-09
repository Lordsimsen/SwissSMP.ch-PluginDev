package ch.swisssmp.weaver;

import ch.swisssmp.city.City;
import ch.swisssmp.city.CitySystem;
import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.*;
import java.util.function.Consumer;

public class CityBanners {

    private static HashMap<UUID, List<Pattern>> cityBanners = new HashMap<>();

    protected static void addCityBanner(UUID cityId, List<Pattern> patterns){
        cityBanners.put(cityId, patterns);
    }

//    private List<Pattern> patterns;
//    private UUID cityId;

//    protected CityBanner(){
//        patterns = new ArrayList<>();
//    }
//
//    protected CityBanner(List<Pattern> patterns, UUID cityId){
//        this.patterns = patterns;
//        this.cityId = cityId;
//    }
//
//    public UUID getCityId(){
//        return cityId;
//    }
//
//    protected void setCityId(UUID cityId){
//        this.cityId = cityId;
//    }
//
//    public List<Pattern> getPatterns(){
//        return patterns;
//    }
//
//    protected void setPatterns(List<Pattern> patterns){
//        this.patterns = patterns;
//    }

    public static boolean isBanner(ItemStack banner, Player player){
        if(!(banner.getItemMeta() instanceof BannerMeta)) return false;
        BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();
        List<Pattern> bannerPatterns = bannerMeta.getPatterns();

        for(UUID id : cityBanners.keySet()){
            City city = CitySystem.getCity(id).orElse(null);
            if(city == null) continue;
            if(!city.isCitizen(player)) continue;
            List<Pattern> cityBannerPatterns = cityBanners.get(id);
            if(cityBannerPatterns.equals(bannerPatterns)) return true;
        }
        return false;
    }

    protected static void unloadBanners(){
        cityBanners.clear();
    }

    protected static void reloadBanners(){
        HTTPRequest request = DataSource.getResponse(WeaverPlugin.getInstance(), WeaverUrl.GET_BANNERS);
        request.onFinish(()->{
            JsonObject json = request.getJsonResponse();
            boolean success = json != null && JsonUtil.getBool("success", json);
            String message = json != null ? JsonUtil.getString("message", json) : null;
            if (message != null) {
                Bukkit.getLogger().info(WeaverPlugin.getPrefix() + " " + message);
            }
            if(success) reloadBanners(json);
        });
    }

    private static void reloadBanners(JsonObject json){
        JsonArray banners = json.getAsJsonArray("banners");
        for(JsonElement banner : banners){
            if(!banner.isJsonObject()) continue;
            reloadBanner((JsonObject) banner);
        }
    }

    public static void reloadBanner(UUID cityId){
        HTTPRequest request = DataSource.getResponse(WeaverPlugin.getInstance(), WeaverUrl.GET_BANNER, new String[]{
                "city=" + cityId
        });
        request.onFinish(()->{
            JsonObject json = request.getJsonResponse();
            boolean success = json != null && JsonUtil.getBool("success", json);
            String message = json != null ? JsonUtil.getString("message", json) : null;
            if (message != null) {
                Bukkit.getLogger().info(WeaverPlugin.getPrefix() + " " + message);
            }
            if(success) reloadBanner(json);
        });
    }

    private static void reloadBanner(JsonObject json){
        UUID cityId = JsonUtil.getUUID("city_id", json);
        List<Pattern> patterns = new ArrayList<>();
        JsonArray patternArray = json.getAsJsonArray("patterns");
        for(JsonElement element : patternArray){
            if(!element.isJsonObject()) continue;
            JsonObject patternSection = element.getAsJsonObject();
            DyeColor color = JsonUtil.getDyeColor("color", patternSection);
            PatternType pattern = JsonUtil.getPattern("type", patternSection);
            if(color==null || pattern==null){
                Bukkit.getLogger().warning(WeaverPlugin.getPrefix()+" Konnte Banner Pattern nicht laden:\n"+element.toString());
                continue;
            }
            patterns.add(new Pattern(color, pattern));
        }
        cityBanners.put(cityId, patterns);
    }

    protected static void registerBanner(List<Pattern> patterns, City city, Consumer<Boolean> callback){
        List<String> arguments = new ArrayList<>();
        arguments.add("city="+city.getUniqueId());
        for(int i = 0; i < patterns.size(); i++){
            Pattern p = patterns.get(i);
            arguments.add("patterns["+i+"][type]="+URLEncoder.encode(p.getPattern().getIdentifier()));
            arguments.add("patterns["+i+"][color]="+ URLEncoder.encode(p.getColor().toString()));
        }
        HTTPRequest request = DataSource.getResponse(WeaverPlugin.getInstance(), WeaverUrl.SAVE_BANNER, arguments.toArray(new String[0]));
        request.onFinish(()->{
            JsonObject json = request.getJsonResponse();
            boolean success = json!=null && JsonUtil.getBool("success", json);
            String message = json!=null ? JsonUtil.getString("message", json) : null;
            if(message!=null){
                Bukkit.getLogger().info(WeaverPlugin.getPrefix()+" "+message);
            }
            if(callback!=null) callback.accept(success);
        });
    }
}
