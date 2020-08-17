package ch.swisssmp.city;

import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.utils.nbt.NBTUtil;
import com.google.gson.JsonObject;
import net.querz.nbt.tag.CompoundTag;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class CityPromotion {
    private final UUID cityId;
    private final String techtreeId;
    private final String levelId;

    protected CityPromotion(UUID cityId, String techtreeId, String levelId){
        this.cityId = cityId;
        this.techtreeId = techtreeId;
        this.levelId = levelId;
    }

    public UUID getCityId(){return cityId;}
    public City getCity(){return CitySystem.getCity(cityId).orElse(null);}
    public String getTechtreeId(){return techtreeId;}
    public Techtree getTechtree(){return CitySystem.getTechtree(techtreeId).orElse(null);}
    public String getLevelId(){return levelId;}
    public CityLevel getLevel(){return CitySystem.getTechtree(techtreeId).flatMap(t -> t.getLevel(levelId)).orElse(null);}

    protected static Optional<CityPromotion> get(ItemStack itemStack){
        CompoundTag tag = ItemUtil.getData(itemStack);
        if(tag==null || !tag.containsKey(CityLevel.LEVEL_PROPERTY)) return Optional.empty();
        CompoundTag levelSection = tag.getCompoundTag(CityLevel.LEVEL_PROPERTY);
        UUID cityId = NBTUtil.getUUID("city_id", levelSection);
        String techtreeId = levelSection.getString("techtree_id");
        String levelId = levelSection.getString("level_id");
        return cityId!=null && techtreeId!=null && levelId!=null ? Optional.of(new CityPromotion(cityId, techtreeId, levelId)) : Optional.empty();
    }

    protected static Optional<CityPromotion> load(JsonObject json){
        UUID cityId = JsonUtil.getUUID("city_id", json);
        String techtreeId = JsonUtil.getString("techtree_id", json);
        String levelId = JsonUtil.getString("level_id", json);
        return cityId!=null && techtreeId!=null && levelId!=null
                ? Optional.of(new CityPromotion(cityId,techtreeId,levelId))
                : Optional.empty();
    }
}
