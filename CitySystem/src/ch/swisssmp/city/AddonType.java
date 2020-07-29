package ch.swisssmp.city;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class AddonType {

    private final String addonId;
    private String name;
    private List<String> shortDescription;
    private List<String> requirementsDescription;
    private List<String> offersDescription;
    private String livemapIconUrl;
    private final HashSet<String> synonyms = new HashSet<String>();

    // conditions
    private int cityLevel;
    private String[] requiredAddons;

    // unlocking
    private AddonUnlockTrade[] unlockTrades;
    private boolean autoActivate;

    private JsonObject configuration;

    protected AddonType(String addonId) {
        this.addonId = addonId;
    }

    public String getAddonId() {
        return addonId;
    }

    public String getIconId() {
        return "addon_" + addonId;
    }

    public String getLivemapIconUrl() {
        return livemapIconUrl;
    }

    public String getName() {
        return name;
    }

    public List<String> getShortDescription(){
        return shortDescription;
    }

    public List<String> getRequirementsDescription(){
        return requirementsDescription;
    }

    public List<String> getOffersDescription(){
        return offersDescription;
    }

    public int getCityLevel() {
        return this.cityLevel;
    }

    public String[] getRequiredAddons(){
        return requiredAddons;
    }

    public Collection<String> getSynonyms() {
        return this.synonyms;
    }

    public AddonUnlockTrade[] getUnlockTrades(){return unlockTrades;}

    public boolean getAutoActivate(){return autoActivate;}

    public JsonObject getConfiguration() {
        return configuration;
    }

    public CustomItemBuilder getItemBuilder(){
        CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder("addon_"+ addonId);
        if(itemBuilder==null){
            itemBuilder = new CustomItemBuilder();
            itemBuilder.setMaterial(Material.BOOK);
        }
        itemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_ENCHANTS);
        itemBuilder.setDisplayName(ChatColor.AQUA+name);

        return itemBuilder;
    }

    public ItemStack getItemStack(){return getItemBuilder().build();}

    private void loadData(JsonObject json) {
        this.name = JsonUtil.getString("name", json);
        this.shortDescription = JsonUtil.getStringList("short_description", json);
        this.requirementsDescription = JsonUtil.getStringList("requirements_description", json);
        this.offersDescription = JsonUtil.getStringList("offers_description", json);
        this.livemapIconUrl = JsonUtil.getString("icon", json);
        this.synonyms.clear();
        if (json.has("synonyms")) {
            this.synonyms.addAll(JsonUtil.getStringList("synonyms", json));
        }
        this.cityLevel = JsonUtil.getInt("level", json);
        this.requiredAddons = json.has("required_addons")
                ? StreamSupport.stream(json.getAsJsonArray("required_addons").spliterator(), false)
                .map(JsonElement::toString)
                .toArray(String[]::new)
                : new String[0];
        this.unlockTrades = AddonUnlockTrades.get(json);
        this.autoActivate = JsonUtil.getBool("auto_activate", json);
        this.configuration = json.getAsJsonObject("configuration");
    }

    protected static Optional<AddonType> load(JsonObject json) {
        String addonId = JsonUtil.getString("addon_id", json);
        if (addonId == null) return Optional.empty();
        AddonType type = new AddonType(addonId);
        type.loadData(json);
        return Optional.of(type);
    }
}
