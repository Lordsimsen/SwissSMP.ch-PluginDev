package ch.swisssmp.knightstournament;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.utils.nbt.NBTTagCompound;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class LoanerEquipment {

    private static final String LOANER_EQUIPMENT_KEY = "_PackedItems";

    private static HashMap<Player,LoanerEquipment> equipments = new HashMap<Player,LoanerEquipment>();

    private final Player player;
    private AbstractHorse horse;
    private ItemStack lance;
    private ItemStack shield;

    private LoanerEquipment(Player player){
        this.player = player;
    }

    private void initialize(){
        CustomItemBuilder lanceBuilder = CustomItems.getCustomItemBuilder(TournamentLance.bareCustomEnum);
        ItemStack lance = lanceBuilder.build();
        packItems(lance, player.getInventory().getContents());
        this.lance = lance;
        this.shield = new ItemStack(Material.SHIELD);
        player.getInventory().clear();
        player.getInventory().setItemInMainHand(lance);
        player.getInventory().setItemInOffHand(shield);

        AbstractHorse horse = (AbstractHorse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
        horse.setTamed(true);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        this.horse = horse;
        horse.addPassenger(player);
    }

    public Player getPlayer(){
        return player;
    }

    public AbstractHorse getHorse(){
        return horse;
    }

    public ItemStack getLance(){
        return lance;
    }

    public ItemStack getShield(){
        return shield;
    }

    protected void remove(){
        equipments.remove(player);
        HashMap<Integer,ItemStack> unpackedItems = unpackItems(lance);
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        for(Map.Entry<Integer,ItemStack> entry : unpackedItems.entrySet()){
            inventory.setItem(entry.getKey(),entry.getValue());
        }
        horse.remove();
    }

    private static HashMap<Integer,ItemStack> unpackItems(ItemStack itemStack){
        NBTTagCompound nbt = ItemUtil.getData(itemStack);
        String serializedJson = nbt.hasKey(LOANER_EQUIPMENT_KEY) ? nbt.getString(LOANER_EQUIPMENT_KEY) : null;
        if(serializedJson==null) return null;
        JsonObject json = JsonUtil.parse(serializedJson);
        if(json==null || !json.has("items") || !json.get("items").isJsonArray()){
            return null;
        }

        JsonArray itemsArray = json.get("items").getAsJsonArray();
        HashMap<Integer,ItemStack> items = new HashMap<Integer,ItemStack>();
        for(JsonElement element : itemsArray){
            if(!element.isJsonObject()) continue;
            JsonObject entry = element.getAsJsonObject();
            int slot = entry.get("s").getAsInt();
            ItemStack entryStack = ItemUtil.deserialize(entry.get("i").getAsString());
            if(entryStack==null) continue;
            items.put(slot, entryStack);
        }
        return items;
    }

    private static void packItems(ItemStack itemStack, ItemStack[] itemStacks){
        JsonObject json = new JsonObject();
        JsonArray itemsArray = new JsonArray();
        for(int i = 0; i < itemStacks.length; i++){
            ItemStack entryStack = itemStacks[i];
            if(entryStack==null) continue;
            String serialized = ItemUtil.serialize(entryStack);
            JsonObject itemJson = new JsonObject();
            itemJson.addProperty("s", i);
            itemJson.addProperty("i", serialized);
            itemsArray.add(itemJson);
        }
        json.add("items", itemsArray);
        ItemUtil.setString(itemStack, LOANER_EQUIPMENT_KEY, json.toString());
    }

    protected static LoanerEquipment get(Player player){
        if(equipments.containsKey(player)){
            return equipments.get(player);
        }
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if(itemStack==null) return null;
        if(ItemUtil.getString(itemStack, LOANER_EQUIPMENT_KEY)==null){
            return null;
        }
        LoanerEquipment equipment = new LoanerEquipment(player);
        equipment.lance = player.getInventory().getItemInMainHand();
        equipment.shield = player.getInventory().getItemInOffHand();
        equipment.horse = player.getVehicle()!=null && player.getVehicle() instanceof AbstractHorse
                ? (AbstractHorse)player.getVehicle()
                : null;
        equipments.put(player,equipment);
        return equipment;
    }

    protected static LoanerEquipment give(Player player){
        if(equipments.containsKey(player)){
            return equipments.get(player);
        }
        LoanerEquipment equipment = new LoanerEquipment(player);
        equipment.initialize();
        return equipment;
    }
}
