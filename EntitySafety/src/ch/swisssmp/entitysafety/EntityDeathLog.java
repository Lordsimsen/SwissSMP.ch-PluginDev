package ch.swisssmp.entitysafety;

import ch.swisssmp.utils.EntityUtil;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.entity.Entity;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

public class EntityDeathLog {

    private static final int MAX_LOG_SIZE = 1000;

    private final ArrayList<EntityDeathLogEntry> entries = new ArrayList<>();

    protected EntityDeathLogEntry add(Entity entity){
        UUID uuid = UUID.randomUUID();
        JsonObject json = EntityUtil.serialize(entity);
        long timestamp = System.currentTimeMillis();
        EntityDeathLogEntry entry = new EntityDeathLogEntry(uuid, json, timestamp);
        entries.add(entry);
        truncate();
        return entry;
    }

    protected void remove(UUID uuid){
        Optional<EntityDeathLogEntry> query = get(uuid);
        if(!query.isPresent()) return;
        entries.remove(query.get());
    }

    private void truncate(){
        if(entries.size()<=MAX_LOG_SIZE) return;
        entries.sort(Comparator.comparingLong(EntityDeathLogEntry::getTimestamp).reversed());
        entries.removeAll(entries.subList(MAX_LOG_SIZE,entries.size()-1));
    }

    protected void save(){
        File file = getFile();
        JsonObject data = new JsonObject();
        JsonArray entriesArray = new JsonArray();
        for(EntityDeathLogEntry entry : entries){
            entriesArray.add(entry.toJson());
        }
        data.add("entries", entriesArray);
        JsonUtil.save(file,data);
    }

    protected static EntityDeathLog load(){
        File file = getFile();
        JsonObject json = JsonUtil.parse(file);
        if(json==null) json = new JsonObject();
        JsonArray entries = json.has("entries") ? json.get("entries").getAsJsonArray() : new JsonArray();
        EntityDeathLog result = new EntityDeathLog();
        for(JsonElement entry : entries){
            if(!entry.isJsonObject()) continue;
            result.entries.add(EntityDeathLogEntry.get(entry.getAsJsonObject()));
        }
        return result;
    }

    protected static File getFile(){
        return new File(EntitySafetyPlugin.getInstance().getDataFolder(), "log.json");
    }

    protected Optional<EntityDeathLogEntry> get(UUID uuid){
        return entries.stream().filter(e->e.getUniqueId().equals(uuid)).findAny();
    }
}
