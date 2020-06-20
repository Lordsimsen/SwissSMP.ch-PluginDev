package ch.swisssmp.knightstournament;

import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class LoanerData {
    private final UUID playerUid;

    private final HashMap<Integer,ItemStack> items;

    protected LoanerData(UUID uid, HashMap<Integer,ItemStack> items){
        this.playerUid = uid;

        this.items = items;
    }

    public void apply(Player player){
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        for(Map.Entry<Integer,ItemStack> entry : items.entrySet()){
            inventory.setItem(entry.getKey(),entry.getValue());
        }

        if(player.getVehicle()!=null &&
                player.getVehicle() instanceof AbstractHorse &&
                LoanerEquipment.isLoaner((AbstractHorse) player.getVehicle())){
            AbstractHorse horse = (AbstractHorse) player.getVehicle();
            PersistentDataContainer data = horse.getPersistentDataContainer();
            NamespacedKey key = LoanerEquipment.getLoanerHorseKey();
            data.remove(key);
            horse.removePassenger(player);
            horse.remove();
        }

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_HORSE_ARMOR, SoundCategory.NEUTRAL, 0.2f, 1);
    }

    public boolean save(){
        JsonObject json = new JsonObject();
        JsonUtil.set("uuid", playerUid, json);
        JsonArray itemsArray = new JsonArray();
        for(Map.Entry<Integer,ItemStack> entry : items.entrySet()){
            JsonObject itemEntry = new JsonObject();
            JsonUtil.set("slot", entry.getKey(), itemEntry);
            JsonUtil.set("item", entry.getValue(), itemEntry);
            itemsArray.add(itemEntry);
        }
        json.add("items", itemsArray);
        File file = getPlayerFile(playerUid);
        return JsonUtil.save(file, json);
    }

    public boolean delete(){
        File file = getPlayerFile(playerUid);
        if(!file.exists()) return true;
        return file.delete();
    }

    protected static LoanerData of(Player player){
        HashMap<Integer,ItemStack> items = new HashMap<>();
        PlayerInventory inventory = player.getInventory();
        for(int i = 0; i < inventory.getSize(); i++){
            ItemStack itemStack = inventory.getItem(i);
            if(itemStack==null || itemStack.getType() == Material.AIR) continue;
            items.put(i, itemStack);
        }
        return new LoanerData(player.getUniqueId(), items);
    }

    protected static Optional<LoanerData> load(Player player){
        return load(getPlayerFile(player));
    }

    protected static Optional<LoanerData> load(File file){
        if(!file.exists()){
            return Optional.empty();
        }
        JsonObject json = JsonUtil.parse(file);
        if(json==null){
            Bukkit.getLogger().warning(KnightsTournamentPlugin.getPrefix()+" Invalid Json: "+file.getAbsoluteFile());
            return Optional.empty();
        }
        LoanerData playerData = load(json);
        return playerData!=null ? Optional.of(playerData) : Optional.empty();
    }

    protected static LoanerData load(JsonObject json){
        UUID playerUid;
        try{
            String playerUidString = JsonUtil.getString("uuid", json);
            if(playerUidString==null){
                Bukkit.getLogger().warning(KnightsTournamentPlugin.getPrefix()+" Invalid UUID: "+json);
                return null;
            }
            playerUid = UUID.fromString(playerUidString);
        }
        catch(Exception e){
            e.printStackTrace();
            Bukkit.getLogger().warning(KnightsTournamentPlugin.getPrefix()+" Invalid Json: "+json);
            return null;
        }

        HashMap<Integer,ItemStack> items = new HashMap<>();
        for(JsonElement element : json.getAsJsonArray("items")){
            if(!element.isJsonObject()) continue;
            JsonObject itemEntry = element.getAsJsonObject();
            int slot = JsonUtil.getInt("slot", itemEntry);
            ItemStack itemStack = JsonUtil.getItemStack("item", itemEntry);
            if(itemStack==null) continue;
            items.put(slot, itemStack);
        }
        return new LoanerData(playerUid, items);
    }

    public static File getPlayerFile(Player player){
        return getPlayerFile(player.getUniqueId());
    }

    public static File getPlayerFile(UUID playerUid){
        return new File(KnightsTournamentPlugin.getInstance().getDataFolder(), "loaner_data/"+playerUid+".json");
    }
}
