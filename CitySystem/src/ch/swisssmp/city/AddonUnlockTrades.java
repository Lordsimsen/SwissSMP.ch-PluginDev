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
            AddonUnlockTrade trade = AddonUnlockTrade.load(AddonUnlockTrade.UnlockType.PERPETUAL, json.getAsJsonObject("cost")).orElse(null);
            if(trade!=null) result.add(trade);
            else{
                Bukkit.getLogger().warning(CitySystemPlugin.getPrefix()+" Konnte AddonUnlockTrade (Kaufpreis) nicht laden:\n"+json.getAsJsonObject("cost").toString());
            }
        }
        if(json.has("licenses")){
            JsonArray licensesSection = json.getAsJsonArray("licenses");
            for(JsonElement element : licensesSection){
                if(!element.isJsonObject()) continue;
                JsonObject licenseSection = element.getAsJsonObject();
                AddonUnlockTrade trade = AddonUnlockTrade.load(AddonUnlockTrade.UnlockType.RENTAL, licenseSection).orElse(null);
                if(trade==null){
                    Bukkit.getLogger().warning(CitySystemPlugin.getPrefix()+" Konnte AddonUnlockTrade (Lizenz) nicht laden:\n"+element.toString());
                    continue;
                }
                result.add(trade);
            }
        }

        return result.toArray(new AddonUnlockTrade[0]);
    }
}
