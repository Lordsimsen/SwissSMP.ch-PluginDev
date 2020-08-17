package ch.swisssmp.city;

import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

class CityPromotions {
    private static final Set<CityPromotion> promotions = new HashSet<>();

    protected static boolean check(UUID cityId, String techtreeId, String levelId){
        return promotions.stream().anyMatch(p->p.getCityId().equals(cityId) && p.getTechtreeId().equals(techtreeId) && p.getLevelId().equals(levelId));
    }

    private static Optional<CityPromotion> get(UUID cityId, String techtreeId, String levelId){
        return promotions.stream().filter(p->p.getCityId().equals(cityId) && p.getTechtreeId().equals(techtreeId) && p.getLevelId().equals(levelId)).findAny();
    }

    protected static void add(UUID cityId, String techtreeId, String levelId, Consumer<Boolean> callback){
        if(check(cityId,techtreeId,levelId)){
            callback.accept(true);
            return;
        }

        add(new CityPromotion(cityId,techtreeId,levelId), callback);
    }

    protected static void add(CityPromotion promotion, Consumer<Boolean> callback){
        HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), CitySystemUrl.ADD_CITY_PROMOTION, new String[]{
                "city="+promotion.getCityId(),
                "techtree="+ URLEncoder.encode(promotion.getTechtreeId()),
                "level="+URLEncoder.encode(promotion.getLevelId())
        });
        request.onFinish(()->{
            JsonObject json = request.getJsonResponse();
            boolean success = json!=null && JsonUtil.getBool("success", json);
            String message = json!=null ? JsonUtil.getString("message", json) : null;
            if(message!=null){
                Bukkit.getLogger().info(CitySystemPlugin.getPrefix()+" "+message);
            }
            if(success){
                promotions.add(promotion);
            }
            if(callback!=null) callback.accept(success);
        });
    }

    protected static void remove(UUID cityId, String techtreeId, String levelId, Consumer<Boolean> callback){
        CityPromotion promotion = get(cityId, techtreeId, levelId).orElse(null);
        if(promotion==null){
            callback.accept(true);
            return;
        }

        remove(promotion, callback);
    }

    protected static void remove(CityPromotion promotion, Consumer<Boolean> callback){
        HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), CitySystemUrl.REMOVE_CITY_PROMOTION, new String[]{
                "city="+promotion.getCityId(),
                "techtree="+ URLEncoder.encode(promotion.getTechtreeId()),
                "level="+URLEncoder.encode(promotion.getLevelId())
        });
        request.onFinish(()->{
            JsonObject json = request.getJsonResponse();
            boolean success = json!=null && JsonUtil.getBool("success", json);
            String message = json!=null ? JsonUtil.getString("message", json) : null;
            if(message!=null){
                Bukkit.getLogger().info(CitySystemPlugin.getPrefix()+" "+message);
            }
            if(success){
                promotions.remove(promotion);
            }
            if(callback!=null) callback.accept(success);
        });
    }

    protected static void loadAll(){
        loadAll((Consumer<Boolean>) null);
    }

    protected static void loadAll(Consumer<Boolean> callback){
        HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), CitySystemUrl.GET_CITY_PROMOTIONS);
        request.onFinish(()->{
            JsonObject json = request.getJsonResponse();
            boolean success = json!=null && JsonUtil.getBool("success", json);
            String message = json!=null ? JsonUtil.getString("message", json) : null;
            if(message!=null){
                Bukkit.getLogger().info(CitySystemPlugin.getPrefix()+" "+message);
            }
            if(success){
                loadAll(json.getAsJsonArray("promotions"));
            }
            if(callback!=null) callback.accept(success);
        });
    }

    protected static void loadAll(JsonArray promotionsArray){
        promotions.clear();
        for(JsonElement element : promotionsArray){
            if(!element.isJsonObject()) continue;
            CityPromotion promotion = CityPromotion.load(element.getAsJsonObject()).orElse(null);
            if(promotion==null){
                Bukkit.getLogger().warning(CitySystemPlugin.getPrefix()+" Konnte CityPromotion nicht laden:\n"+element.toString());
                continue;
            }

            promotions.add(promotion);
        }
    }

    protected static void unloadAll(){
        promotions.clear();
    }
}
