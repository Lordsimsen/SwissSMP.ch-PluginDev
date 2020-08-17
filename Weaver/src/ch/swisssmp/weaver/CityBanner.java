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
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.*;
import java.util.function.Consumer;

public class CityBanner {
    
    private static final List<CityBanner> cityBanners = new ArrayList<>();

    protected static void addCityBanner(UUID cityId, DyeColor baseColor, List<Pattern> patterns){
        cityBanners.add(new CityBanner(cityId, baseColor, patterns));
    }

    private final UUID cityId;
    private final DyeColor baseColor;
    private final List<Pattern> patterns;

    private CityBanner(UUID cityId, DyeColor baseColor, List<Pattern> patterns){
        this.cityId = cityId;
        this.baseColor = baseColor;
        this.patterns = patterns;
    }

    public static DyeColor getDyeColor(Material banner){
        switch(banner){
            case BLACK_BANNER:
            case BLACK_WALL_BANNER:{
                return DyeColor.BLACK;
            }
            case GRAY_BANNER:
            case GRAY_WALL_BANNER:{
                return DyeColor.GRAY;
            }
            case LIGHT_GRAY_BANNER:
            case LIGHT_GRAY_WALL_BANNER:{
                return DyeColor.LIGHT_GRAY;
            }
            case WHITE_BANNER:
            case WHITE_WALL_BANNER:{
                return DyeColor.WHITE;
            }
            case GREEN_BANNER:
            case GREEN_WALL_BANNER:{
                return DyeColor.GREEN;
            }
            case LIME_BANNER:
            case LIME_WALL_BANNER:{
                return DyeColor.LIME;
            }
            case CYAN_BANNER:
            case CYAN_WALL_BANNER:{
                return DyeColor.CYAN;
            }
            case LIGHT_BLUE_BANNER:
            case LIGHT_BLUE_WALL_BANNER:{
                return DyeColor.LIGHT_BLUE;
            }
            case BLUE_BANNER:
            case BLUE_WALL_BANNER:{
                return DyeColor.BLUE;
            }
            case MAGENTA_BANNER:
            case MAGENTA_WALL_BANNER:{
                return DyeColor.MAGENTA;
            }
            case PURPLE_BANNER:
            case PURPLE_WALL_BANNER:{
                return DyeColor.PURPLE;
            }
            case PINK_BANNER:
            case PINK_WALL_BANNER:{
                return DyeColor.PINK;
            }
            case RED_BANNER:
            case RED_WALL_BANNER:{
                return DyeColor.RED;
            }
            case YELLOW_BANNER:
            case YELLOW_WALL_BANNER:{
                return DyeColor.YELLOW;
            }
            case ORANGE_BANNER:
            case ORANGE_WALL_BANNER:{
                return DyeColor.ORANGE;
            }
            case BROWN_BANNER:
            case BROWN_WALL_BANNER:{
                return DyeColor.BROWN;
            }
            default: return null;
        }
    }

    public static boolean isBanner(ItemStack banner, Player player){
        if(!(banner.getItemMeta() instanceof BannerMeta)) return false;
        BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();
        List<Pattern> bannerPatterns = bannerMeta.getPatterns();
        DyeColor bannerBaseColor = getDyeColor(banner.getType());

        for(CityBanner cityBanner : cityBanners){
            City city = CitySystem.getCity(cityBanner.cityId).orElse(null);
            if(city == null) continue;
            if(!city.isCitizen(player)) continue;
            if(cityBanner.patterns.equals(bannerPatterns) && cityBanner.baseColor.equals(bannerBaseColor)) return true;
        }
        return false;
    }

    protected static void unloadBanners(){
        cityBanners.clear();
    }

    protected static void reloadBanners(Consumer<Boolean> callback){
        HTTPRequest request = DataSource.getResponse(WeaverPlugin.getInstance(), WeaverUrl.GET_BANNERS);
        request.onFinish(()->{
            JsonObject json = request.getJsonResponse();
            boolean success = json != null && JsonUtil.getBool("success", json);
            String message = json != null ? JsonUtil.getString("message", json) : null;
            if (message != null) {
                Bukkit.getLogger().info(WeaverPlugin.getPrefix() + " " + message);
            }
            if(success) reloadBanners(json.getAsJsonArray("banners"));
            if(callback != null) callback.accept(success);
        });
    }

    private static void reloadBanners(JsonArray banners){
        for(JsonElement banner : banners){
            if(!banner.isJsonObject()) continue;
            reloadBanner(banner.getAsJsonObject());
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
        DyeColor baseColor =  JsonUtil.getDyeColor("base_color", json);
        List<Pattern> patterns = new ArrayList<>();
        if(json.has("patterns")) {
            JsonArray patternArray = json.getAsJsonArray("patterns");
            for (JsonElement element : patternArray) {
                if (!element.isJsonObject()) continue;
                JsonObject patternSection = element.getAsJsonObject();
                DyeColor color = JsonUtil.getDyeColor("color", patternSection);
                PatternType pattern = JsonUtil.getPattern("type", patternSection);
                if (color == null || pattern == null) {
                    Bukkit.getLogger().warning(WeaverPlugin.getPrefix() + " Konnte Banner nicht laden (" + color + ", " + pattern + "):\n" + element.toString());
                    continue;
                }
                patterns.add(new Pattern(color, pattern));
            }
        }
        for(CityBanner banner : cityBanners){
            if(!banner.cityId.equals(cityId)) continue;
            cityBanners.remove(banner);
            cityBanners.add(new CityBanner(cityId, baseColor, patterns));
        }
    }

    protected static void registerBanner(DyeColor baseColor, List<Pattern> patterns, City city, Consumer<Boolean> callback){
        List<String> arguments = new ArrayList<>();
        arguments.add("city="+city.getUniqueId());
        arguments.add("base_color="+baseColor.getColor().asRGB());
        for(int i = 0; i < patterns.size(); i++){
            Pattern p = patterns.get(i);
            arguments.add("patterns["+i+"][type]="+URLEncoder.encode(p.getPattern().getIdentifier()));
            arguments.add("patterns["+i+"][color]="+ p.getColor().getColor().asRGB());
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
