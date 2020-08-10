package ch.swisssmp.city.ceremony.promotion;

import ch.swisssmp.city.CityLevel;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;

public class PromotionCeremonyData {

    private final CityLevel level;
    private final ItemStack[] tribute;

    private final int promotionPlayerCount;
    private final int promotionHaybaleCount;

    private final int climaxExplosionCycles;
    private final int fireworkCycles;

    public PromotionCeremonyData(CityLevel cityLevel){
        JsonObject levelConfiguration = cityLevel.getConfiguration();
        JsonObject ceremonySection = levelConfiguration.has("ceremony") ? levelConfiguration.getAsJsonObject("ceremony") : new JsonObject();

        this.level = cityLevel;
        this.tribute = cityLevel.getCost();

        this.promotionPlayerCount = JsonUtil.getInt("promotion_playercount", ceremonySection);
        this.promotionHaybaleCount = JsonUtil.getInt("promotion_haybalecount", ceremonySection);
        this.climaxExplosionCycles = JsonUtil.getInt("climax_explosion_cycles", ceremonySection);
        this.fireworkCycles = JsonUtil.getInt("firework_cycles", ceremonySection);
    }

    public CityLevel getLevel(){return level;}

    public ItemStack[] getTribute(){
        return tribute;
    }

    public int getPromotionPlayerCount(){
        return promotionPlayerCount;
    }

    public int getPromotionHaybaleCount(){
        return promotionHaybaleCount;
    }

    public int getClimaxExplosionCycles(){
        return climaxExplosionCycles;
    }

    public int getFireworkCycles(){
        return fireworkCycles;
    }

    public static PromotionCeremonyData create(CityLevel level){
        return new PromotionCeremonyData(level);
    }
}
