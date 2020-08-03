package ch.swisssmp.city;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class CityLevel {

    private final Techtree techtree;
    private final String id;
    private final String name;

    private final int columns;
    private final int rows;

    private final int minPopulation;
    private final int minAddonCount;
    private final String[] requiredAddons;
    private final ItemStack[] cost;

    private final JsonObject configuration;

    private CityLevel(Techtree techtree, String id, String name, int columns, int rows, int minPopulation, int minAddonCount, String[] requiredAddons, ItemStack[] cost, JsonObject configuration){
        this.techtree = techtree;
        this.id = id;
        this.name = name;

        this.columns = columns;
        this.rows = rows;

        this.minPopulation = minPopulation;
        this.minAddonCount = minAddonCount;
        this.requiredAddons = requiredAddons;
        this.cost = cost;
        this.configuration = configuration;
    }

    public Techtree getTechtree(){return this.techtree;}

    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public int getColumnCount(){return columns;}
    public int getRowCount(){return rows;}

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

    public Collection<AddonType> getAddonTypes(){
        return techtree.getAddonTypes(this);
    }

    protected static Optional<CityLevel> load(Techtree techtree, JsonObject json){
        if(json==null) return Optional.empty();
        String id = JsonUtil.getString("id", json);
        String name = JsonUtil.getString("name", json);
        int columns = JsonUtil.getInt("columns", json);
        int rows = JsonUtil.getInt("rows", json);
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
        return Optional.of(new CityLevel(techtree, id, name, columns, rows, minPopulation, minAddonCount, requiredAddons, cost, configuration));
    }
}
