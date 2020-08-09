package ch.swisssmp.city;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;

public class AddonUnlockTrade {
    private final List<ItemStack> items = new ArrayList<ItemStack>();
    private final UnlockType type;
    private String description;

    private AddonUnlockTrade(UnlockType type) {
        this.type = type;
    }

    public List<ItemStack> getItems() {
        return new ArrayList<ItemStack>(items);
    }

    public UnlockType getUnlockType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public MerchantRecipe getRecipe(ItemStack resultTemplate) {
        ItemStack resultStack = createResultStack(resultTemplate);
        MerchantRecipe result = new MerchantRecipe(resultStack, 1);
        result.setExperienceReward(false);
        result.setMaxUses(1);
        result.setUses(0);
        result.setIngredients(items);
        return result;
    }

    private ItemStack createResultStack(ItemStack resultTemplate) {
        ItemStack result = resultTemplate.clone();
        result.setAmount(1);
        ItemMeta itemMeta = result.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + itemMeta.getDisplayName());
        List<String> description = new ArrayList<String>();
        description.add(ChatColor.GRAY + this.description);
        itemMeta.setLore(description);
        result.setItemMeta(itemMeta);
        return result;
    }

    private void loadData(JsonObject json) {
        this.items.clear();
        this.description = JsonUtil.getString("description", json);
        if (json.has("items")) {
            JsonElement itemsSection = json.get("items");
            if(itemsSection.isJsonArray()){
                for (JsonElement element : itemsSection.getAsJsonArray()) {
                    if (!element.isJsonObject()) continue;
                    JsonObject itemSection = element.getAsJsonObject();
                    ItemStack itemStack = getItem(itemSection);
                    if (itemStack == null) continue;
                    items.add(itemStack);
                }
            }
            else if(itemsSection.isJsonObject()){
                JsonObject itemSection = itemsSection.getAsJsonObject();
                ItemStack itemStack = getItem(itemSection);
                if (itemStack != null) items.add(itemStack);
            }
        }
        else{
            ItemStack itemStack = getItem(json);
            if (itemStack != null) items.add(itemStack);
        }
    }

    private static ItemStack getItem(JsonObject itemSection) {
        CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder(itemSection);
        if (itemBuilder == null) return null;
        return itemBuilder.build();
    }

    public static Optional<AddonUnlockTrade> load(UnlockType type, JsonObject json) {
        AddonUnlockTrade trade = new AddonUnlockTrade(type);
        trade.loadData(json);
        if (trade.items.size() == 0) return Optional.empty();
        return Optional.of(trade);
    }

    protected enum UnlockType {
        PERPETUAL,
        RENTAL;

        protected static UnlockType get(String value) {
            if (value.toLowerCase().equals("rental")) return RENTAL;
            return PERPETUAL;
        }
    }
}
