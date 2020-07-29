package ch.swisssmp.city;

import java.util.ArrayList;
import java.util.List;

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
    private final String description;

    private AddonUnlockTrade(UnlockType type, JsonObject json) {
        this.type = type;
        this.description = JsonUtil.getString("description", json);
        JsonArray itemsArray = json.has("items") && json.get("items").isJsonArray() ? json.getAsJsonArray("items") : null;
        if (itemsArray != null) {
            for (JsonElement element : itemsArray) {
                if (!element.isJsonObject()) continue;
                JsonObject itemSection = element.getAsJsonObject();
                try {
                    ItemStack itemStack = getItem(itemSection);
                    if (itemStack == null) continue;
                    items.add(itemStack);
                } catch (Exception ignored) {

                }
            }
        }
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

    private static ItemStack getItem(JsonObject itemSection) {
        CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder(itemSection);
        if (itemBuilder == null) return null;
        return itemBuilder.build();
    }

    public static AddonUnlockTrade get(UnlockType type, JsonObject json) {
        AddonUnlockTrade trade = new AddonUnlockTrade(type, json);
        if (trade.items.size() == 0) return null;
        return trade;
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
