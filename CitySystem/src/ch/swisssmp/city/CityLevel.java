package ch.swisssmp.city;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.stream.StreamSupport;

public class CityLevel {

    private final String id;
    private final String name;

    private final int minPopulation;
    private final int minAddonCount;
    private final String[] requiredAddons;
    private final ItemStack[] cost;

    private final JsonObject configuration;

    private CityLevel(String id, String name, int minPopulation, int minAddonCount, String[] requiredAddons, ItemStack[] cost, JsonObject configuration){
        this.id = id;
        this.name = name;
        this.minPopulation = minPopulation;
        this.minAddonCount = minAddonCount;
        this.requiredAddons = requiredAddons;
        this.cost = cost;
        this.configuration = configuration;
    }

    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public int getMinPopulation(){
        return minPopulation;
    }

    public int getMinAddonCount(){
        return minAddonCount;
    }

    public String[] getRequiredAddons(){
        return requiredAddons;
    }

    public ItemStack[] getCost(){
        return cost;
    }

    public JsonObject getConfiguration(){
        return configuration;
    }

    protected static Optional<CityLevel> load(JsonObject json){
        if(json==null) return Optional.empty();
        String id = JsonUtil.getString("id", json);
        String name = JsonUtil.getString("name", json);
        if(id==null || name==null) return Optional.empty();
        JsonObject configuration = json.has("configuration") && json.get("configuration").isJsonObject() ? json.getAsJsonObject("configuration") : new JsonObject();
        int minPopulation = configuration.has("population") ? JsonUtil.getInt("population", configuration) : 0;
        int minAddonCount = configuration.has("addon_count") ? JsonUtil.getInt("addon_count", configuration) : 0;
        String[] requiredAddons = configuration.has("addons") ? JsonUtil.getStringList("addons", configuration).toArray(new String[0]) : new String[0];
        ItemStack[] cost = configuration.has("cost")
                ? StreamSupport.stream(configuration.getAsJsonArray("cost").spliterator(), false)
                    .map(element-> CustomItems.getCustomItemBuilder(element.getAsJsonObject()).build())
                    .toArray(ItemStack[]::new)
                : new ItemStack[0];
        return Optional.of(new CityLevel(id, name, minPopulation, minAddonCount, requiredAddons, cost, configuration));
    }
}
