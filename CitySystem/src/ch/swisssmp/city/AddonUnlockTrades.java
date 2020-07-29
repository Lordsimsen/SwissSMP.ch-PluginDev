package ch.swisssmp.city;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

class AddonUnlockTrades {

    protected static AddonUnlockTrade[] get(JsonObject json){
        List<AddonUnlockTrade> result = new ArrayList<>();
        if(json.has("cost") && json.get("cost").isJsonObject()){
            AddonUnlockTrade trade = AddonUnlockTrade.get(AddonUnlockTrade.UnlockType.PERPETUAL, json.getAsJsonObject("cost"));
            if(trade!=null) result.add(trade);
        }
        if(json.has("licenses")){
            JsonArray licensesSection = json.getAsJsonArray("licenses");
            for(JsonElement element : licensesSection){
                if(!element.isJsonObject()) continue;
                JsonObject licenseSection = element.getAsJsonObject();
                AddonUnlockTrade trade = AddonUnlockTrade.get(AddonUnlockTrade.UnlockType.RENTAL, licenseSection);
                if(trade==null){
                    Bukkit.getLogger().warning(CitySystemPlugin.getPrefix()+" Konnte AddonUnlockTrade nicht laden:\n"+element.toString());
                    continue;
                }
                result.add(trade);
            }
        }

        return result.toArray(new AddonUnlockTrade[0]);
    }
}
