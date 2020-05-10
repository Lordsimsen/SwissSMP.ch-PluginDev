package ch.swisssmp.zvierigame.game;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.utils.nbt.NBTTagCompound;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipmentStorer {

    private static final String EQUIPMENT_STORER_KEY = "_PackedItems";

    private static HashMap<Player, EquipmentStorer> equipments = new HashMap<>();

    private final Player player;
    private ItemStack key;


    private EquipmentStorer (Player player){
        this.player = player;
    }

    private void initialize(){
        ItemStack key = CustomItems.getCustomItemBuilder("ZVIERI_ARENA").build();
        ItemMeta keyMeta = key.getItemMeta();
        keyMeta.setDisplayName("Garderoben-Badge");
        List<String> description = new ArrayList<>();
        description.add("Wird am Ende der Runde");
        description.add("gegen deine Items getauscht");
        keyMeta.setLore(description);
        key.setItemMeta(keyMeta);
        packItems(key, player.getInventory().getContents());
        this.key = key;
        player.getInventory().clear();
        player.getInventory().setItemInOffHand(key);
    }

    public Player getPlayer(){
        return player;
    }

    public ItemStack getKey(){
        return key;
    }

    protected void remove(){
        equipments.remove(player);
        HashMap<Integer, ItemStack> unpackedItems = unpackItems(key);
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        for(Map.Entry<Integer, ItemStack> entry : unpackedItems.entrySet()){
            inventory.setItem(entry.getKey(), entry.getValue());
        }
    }

    private static HashMap<Integer, ItemStack> unpackItems(ItemStack itemStack){
        NBTTagCompound nbt = ItemUtil.getData(itemStack);
        String seralizedJson = nbt.hasKey(EQUIPMENT_STORER_KEY) ? nbt.getString(EQUIPMENT_STORER_KEY) : null;
        if(seralizedJson == null) return null;
        JsonObject json = JsonUtil.parse(seralizedJson);
        if(json == null || !json.has("items") || !json.get("items").isJsonArray()){
            return null;
        }

        JsonArray itemsArray = json.get("items").getAsJsonArray();
        HashMap<Integer, ItemStack> items = new HashMap<>();
        for(JsonElement element : itemsArray){
            if(!element.isJsonObject()) continue;
            JsonObject entry = element.getAsJsonObject();
            int slot = entry.get("s").getAsInt();
            ItemStack entryStack = ItemUtil.deserialize(entry.get("i").getAsString());
            if(entryStack == null) continue;
            items.put(slot, entryStack);
        }
        return items;
    }

    private static void packItems(ItemStack itemStack, ItemStack[] itemStacks){
        JsonObject json = new JsonObject();
        JsonArray itemsArray = new JsonArray();
        for(int i = 0; i < itemStacks.length; i++){
            ItemStack entryStack = itemStacks[i];
            if(entryStack == null) continue;
            String serialized = ItemUtil.serialize(entryStack);
            JsonObject itemJson = new JsonObject();
            itemJson.addProperty("s", i);
            itemJson.addProperty("i", serialized);
            itemsArray.add(itemJson);
        }
        json.add("items", itemsArray);
        ItemUtil.setString(itemStack, EQUIPMENT_STORER_KEY, json.toString());
    }

    protected static EquipmentStorer get(Player player){
        if(equipments.containsKey(player)){
            return equipments.get(player);
        }
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if(itemStack == null) return null;
        if(ItemUtil.getString(itemStack, EQUIPMENT_STORER_KEY) == null) return null;
        EquipmentStorer equipment = new EquipmentStorer(player);
        equipment.key = player.getInventory().getItemInMainHand();
        equipments.put(player, equipment);
        return equipment;
    }

    protected static EquipmentStorer give(Player player){
        if(equipments.containsKey(player)){
            return equipments.get(player);
        }
        EquipmentStorer equipment = new EquipmentStorer(player);
        equipment.initialize();
        return equipment;
    }
}
