package ch.swisssmp.city;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.utils.nbt.NBTUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.querz.nbt.tag.CompoundTag;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

public class CityLevel {

    protected static final String LEVEL_PROPERTY = "city_level";

    private final Techtree techtree;
    private final String id;
    private final String name;
    private final ChatColor color;

    private final int columns;
    private final int rows;

    private final int minPopulation;
    private final int minAddonCount;
    private final String[] requiredAddons;
    private final ItemStack[] cost;

    private final JsonObject configuration;

    private CityLevel(Techtree techtree, String id, String name, ChatColor color, int columns, int rows, int minPopulation, int minAddonCount, String[] requiredAddons, ItemStack[] cost, JsonObject configuration){
        this.techtree = techtree;
        this.id = id;
        this.name = name;
        this.color = color;

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

    public ChatColor getColor(){return color;}

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

    public CustomItemBuilder getTokenBuilder(){
        JsonElement tokenSection = this.configuration!=null && this.configuration.has("token") ? this.configuration.get("token") : null;
        CustomItemBuilder result = (tokenSection!=null && tokenSection.isJsonObject())
                ? CustomItems.getCustomItemBuilder(tokenSection.getAsJsonObject())
                : new CustomItemBuilder(Material.BOOK);
        result.setDisplayName(color+name);
        return result;
    }

    public ItemStack getTokenStack(){
        ItemStack result = getTokenBuilder().build();
        CompoundTag tag = ItemUtil.getData(result);
        tag.put(LEVEL_PROPERTY, createLevelTag());
        ItemUtil.setData(result, tag);
        return result;
    }

    public ItemStack getKeyStack(City city){
        ItemStack result = getKeyBuilder(city).build();
        CompoundTag tag = ItemUtil.getData(result);
        tag.put(LEVEL_PROPERTY, createLevelTag(city.getUniqueId()));
        ItemUtil.setData(result, tag);
        return result;
    }

    private CustomItemBuilder getKeyBuilder(City city){
        JsonElement tokenSection = this.configuration!=null && this.configuration.has("key") ? this.configuration.get("key") : null;
        CustomItemBuilder result = (tokenSection!=null && tokenSection.isJsonObject())
                ? CustomItems.getCustomItemBuilder(tokenSection.getAsJsonObject())
                : new CustomItemBuilder(Material.PAPER);
        result.setDisplayName(ChatColor.AQUA+"Stadtschlüssel");
        result.setLore(Arrays.asList(color+name,ChatColor.GRAY+"Zeremonieschlüssel für",ChatColor.GRAY+city.getName()));
        return result;
    }

    private CompoundTag createLevelTag(){
        return createLevelTag(null);
    }

    private CompoundTag createLevelTag(UUID cityId){
        CompoundTag levelSection = new CompoundTag();
        levelSection.putString("techtree_id", this.techtree.getId());
        levelSection.putString("level_id", this.id);
        if(cityId!=null) NBTUtil.set("city_id", cityId, levelSection);
        return levelSection;
    }

    public Collection<AddonType> getAddonTypes(){
        return techtree.getAddonTypes(this);
    }

    protected static Optional<CityLevel> get(ItemStack itemStack){
        CompoundTag tag = ItemUtil.getData(itemStack);
        if(tag==null || !tag.containsKey(LEVEL_PROPERTY)) return Optional.empty();
        CompoundTag levelSection = tag.getCompoundTag(LEVEL_PROPERTY);
        String techtreeId = levelSection.getString("techtree_id");
        String levelId = levelSection.getString("level_id");
        Techtree techtree = CitySystem.getTechtree(techtreeId).orElse(null);
        return techtree!=null ? techtree.getLevel(levelId) : Optional.empty();
    }

    protected static Optional<CityLevel> load(Techtree techtree, JsonObject json){
        if(json==null) return Optional.empty();
        String id = JsonUtil.getString("id", json);
        String name = JsonUtil.getString("name", json);
        int columns = JsonUtil.getInt("columns", json);
        int rows = JsonUtil.getInt("rows", json);
        if(id==null || name==null) return Optional.empty();
        JsonObject configuration = json.has("configuration") && json.get("configuration").isJsonObject() ? json.getAsJsonObject("configuration") : new JsonObject();
        ChatColor color = JsonUtil.getChatColor("color", configuration);
        int minPopulation = configuration.has("population") ? JsonUtil.getInt("population", configuration) : 0;
        int minAddonCount = configuration.has("addon_count") ? JsonUtil.getInt("addon_count", configuration) : 0;
        String[] requiredAddons = configuration.has("addons") ? JsonUtil.getStringList("addons", configuration).toArray(new String[0]) : new String[0];
        ItemStack[] cost = configuration.has("cost")
                ? StreamSupport.stream(configuration.getAsJsonArray("cost").spliterator(), false)
                    .map(element-> CustomItems.getCustomItemBuilder(element.getAsJsonObject()).build())
                    .toArray(ItemStack[]::new)
                : new ItemStack[0];
        return Optional.of(new CityLevel(techtree, id, name, color, columns, rows, minPopulation, minAddonCount, requiredAddons, cost, configuration));
    }
}
