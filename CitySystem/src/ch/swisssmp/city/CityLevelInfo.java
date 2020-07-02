package ch.swisssmp.city;

import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class CityLevelInfo {

    private final City city;

    private CityRank rank;
    private int numericRank;

    private int requiredPlayers;

    private int promotionPlayercount;
    private int ironBlockTribute;
    private int goldBlockTribute;
    private int diamondBlockTribute;
    private int netheriteBlockTribute;
    private int promotionHaybalecount;

    private static List<CityLevelInfo> cityLevelInfoList = new ArrayList<>();

    private CityLevelInfo(City city, JsonObject json){
        this.city = city;
        rank = CityRank.of(JsonUtil.getString("rank", json));
        numericRank = CityRank.of(rank);
        requiredPlayers = JsonUtil.getInt("required_players", json);
        promotionPlayercount = JsonUtil.getInt("promotion_playercount", json);
        ironBlockTribute = JsonUtil.getInt("tribute_iron_block", json);
        goldBlockTribute = JsonUtil.getInt("tribute_gold_block", json);
        diamondBlockTribute = JsonUtil.getInt("tribute_diamond_block", json);
        netheriteBlockTribute = JsonUtil.getInt("tribute_netherite_block", json);
        promotionHaybalecount = JsonUtil.getInt("promotion_haybalecount", json);
    }

    public CityRank getRank(){
        return rank;
    }

    public int getNumericRank(){
        return numericRank;
    }

    public int getRequiredPlayers(){
        return requiredPlayers;
    }

    public int getPromotionPlayercount(){
        return promotionPlayercount;
    }

    public int getIronBlockTribute(){
        return ironBlockTribute;
    }

    public int getGoldBlockTribute(){
        return goldBlockTribute;
    }

    public int getDiamondBlockTribute(){
        return diamondBlockTribute;
    }

    public int getNetheriteBlockTribute(){
        return netheriteBlockTribute;
    }


    public int getPromotionHaybalecount(){
        return promotionHaybalecount;
    }

    public static CityLevelInfo load(City city){
        HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), "get_citylevelinfo.php", new String[]{
                "city_id="+city.getId()});
        request.onFinish(() ->{
            JsonObject json = request.getJsonResponse();
            if(json == null || !json.get("success").getAsBoolean()){
                Bukkit.getLogger().info(CitySystemPlugin.getPrefix() + " Couldn't load CityLevelInfo for City: " + city.getName());
            }
            cityLevelInfoList.add(new CityLevelInfo(city, json));
            return;
        });
        return cityLevelInfoList.get(0); //todo not quite
    }

    public static void save(City city, CityLevelInfo cityLevelInfo){
        HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), city.getName() + "update_citylevelinfo.php");
        request.onFinish(() ->{
            JsonObject json = request.getJsonResponse();
            if(json == null || !json.get("success").getAsBoolean()){
                Bukkit.getLogger().info(CitySystemPlugin.getPrefix() + " Couldn't load CityLevelInfo for City: " + city.getName());
            }
        });
    }


    private enum CityRank{
        Gemeinschaft,
        Stadt,
        Grossstadt,
        Metropole;

        protected static CityRank of(String rankString){
            switch (rankString) {
                case "Gemeinschaft":
                case "gemeinschaft":
                    return Gemeinschaft;
                case "Stadt":
                case "stadt":
                    return Stadt;
                case "Grossstadt":
                case "grossstadt":
                    return Grossstadt;
                case "Metropole":
                case "metropole":
                    return Metropole;
                default: return null;
            }
        }

        protected static int of(CityRank rank){
            switch (rank){
                case Gemeinschaft: return 1;
                case Stadt: return 2;
                case Grossstadt: return 3;
                case Metropole: return 4;
                default: return 0;
            }
        }
    }
}
