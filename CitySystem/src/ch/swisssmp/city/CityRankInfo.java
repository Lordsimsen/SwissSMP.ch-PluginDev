package ch.swisssmp.city;

import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class CityRankInfo {

    private CityRank rank;
    private int numericRank;

    private int requiredPlayers;

    private int promotionPlayers;
    private int promotionTribute;
    private int promotionHaybales;

    private static List<CityRankInfo> cityRankInfoList = new ArrayList<>();

    private CityRankInfo(JsonObject json){
        rank = CityRank.of(json.get("rank").getAsString());
        numericRank = CityRank.of(rank);
        requiredPlayers = json.get("required_players").getAsInt();
        promotionPlayers = json.get("promotion_players").getAsInt();
        promotionTribute = json.get("promotion_tribute").getAsInt();
        promotionHaybales = json.get("promotion_haybales").getAsInt();
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

    public int getPromotionPlayers(){
        return promotionPlayers;
    }

    public int getPromotionTribute(){
        return promotionTribute;
    }

    public int getPromotionHaybales(){
        return promotionHaybales;
    }

    public CityRankInfo load(City city){
        HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), city.getName() + "_rank_info.json");
        request.onFinish(() ->{
            JsonObject json = request.getJsonResponse();
            if(json == null || !json.get("success").getAsBoolean()){
                Bukkit.getLogger().info(CitySystemPlugin.getPrefix() + " Couldn't load CityRankInfo for City: " + city.getName());
            }
            cityRankInfoList.add(new CityRankInfo(json));
            return;
        });
        return cityRankInfoList.get(0); //todo not quite
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
