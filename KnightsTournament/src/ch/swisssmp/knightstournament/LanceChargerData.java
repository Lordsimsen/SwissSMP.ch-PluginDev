package ch.swisssmp.knightstournament;

import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

public class LanceChargerData {
    private final UUID playerUid;

    private final int slot;
    private final ItemStack replaced;

    protected LanceChargerData(UUID uid, int slot, ItemStack replaced){
        this.playerUid = uid;

        this.slot = slot;
        this.replaced = replaced;
    }

    public void apply(Player player){
        if(slot>=0) player.getInventory().setItem(slot, replaced);
    }

    public boolean save(){
        JsonObject json = new JsonObject();
        JsonUtil.set("uuid", playerUid, json);
        JsonUtil.set("slot", slot, json);
        if(replaced!=null) JsonUtil.set("item", replaced, json);
        File file = getPlayerFile(playerUid);
        return JsonUtil.save(file, json);
    }

    public boolean delete(){
        File file = getPlayerFile(playerUid);
        if(!file.exists()) return true;
        return file.delete();
    }

    protected static LanceChargerData of(Player player, int slot, ItemStack replaced){
        return new LanceChargerData(player.getUniqueId(), slot, replaced);
    }

    protected static Optional<LanceChargerData> load(Player player){
        return load(getPlayerFile(player));
    }

    protected static Optional<LanceChargerData> load(File file){
        if(!file.exists()){
            return Optional.empty();
        }
        JsonObject json = JsonUtil.parse(file);
        if(json==null){
            Bukkit.getLogger().warning(KnightsTournamentPlugin.getPrefix()+" Invalid Json: "+file.getAbsoluteFile());
            return Optional.empty();
        }
        LanceChargerData playerData = load(json);
        return playerData!=null ? Optional.of(playerData) : Optional.empty();
    }

    protected static LanceChargerData load(JsonObject json){
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

        int slot = json.has("slot") ? JsonUtil.getInt("slot", json) : -1;
        ItemStack replaced = JsonUtil.getItemStack("replaced", json);
        return new LanceChargerData(playerUid, slot, replaced);
    }

    public static File getPlayerFile(Player player){
        return getPlayerFile(player.getUniqueId());
    }

    public static File getPlayerFile(UUID playerUid){
        return new File(KnightsTournamentPlugin.getInstance().getDataFolder(), "charger_data/"+playerUid+".json");
    }
}
