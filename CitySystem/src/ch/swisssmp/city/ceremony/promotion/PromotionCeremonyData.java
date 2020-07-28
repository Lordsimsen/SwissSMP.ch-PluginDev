package ch.swisssmp.city.ceremony.promotion;

import ch.swisssmp.city.City;
import ch.swisssmp.city.CityLevel;
import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.city.Techtree;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PromotionCeremonyData {

    private final int promotionPlayercount;
    private final int promotionHaybalecount;

    private final int climaxExplosionCycles;
    private final int fireworkCycles;

    private final ItemStack[] tribute;

    private PromotionCeremonyData(CityLevel cityLevel){
        JsonObject levelConfiguration = cityLevel.getConfiguration();
        JsonObject ceremonySection = levelConfiguration.has("ceremony") ? levelConfiguration.getAsJsonObject("ceremony") : new JsonObject();

        promotionPlayercount = JsonUtil.getInt("promotion_playercount", ceremonySection);
        promotionHaybalecount = JsonUtil.getInt("promotion_haybalecount", ceremonySection);
        climaxExplosionCycles = JsonUtil.getInt("climax_explosion_cycles", ceremonySection);
        fireworkCycles = JsonUtil.getInt("firework_cycles", ceremonySection);
        tribute = cityLevel.getCost();
    }

    public int getPromotionPlayercount(){
        return promotionPlayercount;
    }

    public int getPromotionHaybalecount(){
        return promotionHaybalecount;
    }

    public int getClimaxExplosionCycles(){
        return climaxExplosionCycles;
    }

    public int getFireworkCycles(){
        return fireworkCycles;
    }

    public ItemStack[] getTribute(){
        return tribute;
    }

    public static PromotionCeremonyData load(City city){
        String levelId = city.getLevelId();
        Techtree techtree = city.getTechtree();
        int currentLevelIndex = techtree.getLevelIndex(levelId);
        CityLevel newLevel = techtree.getLevel(currentLevelIndex + 1);
        return new PromotionCeremonyData(newLevel);
    }
}
