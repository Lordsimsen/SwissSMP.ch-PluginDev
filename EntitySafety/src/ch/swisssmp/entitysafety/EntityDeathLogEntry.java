package ch.swisssmp.entitysafety;

import com.google.gson.JsonObject;

import java.util.UUID;

public class EntityDeathLogEntry {
    private final UUID uuid;
    private final JsonObject data;
    private final long timestamp;

    protected EntityDeathLogEntry(UUID uuid, JsonObject data, long timestamp){
        this.uuid = uuid;
        this.data = data;
        this.timestamp = timestamp;
    }

    public UUID getUniqueId(){
        return uuid;
    }

    public JsonObject getJsonData(){
        return data;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public JsonObject toJson(){
        JsonObject result = new JsonObject();
        result.addProperty("uuid", uuid.toString());
        result.add("data", data);
        result.addProperty("timestamp", timestamp);
        return result;
    }

    protected static EntityDeathLogEntry get(JsonObject json){
        UUID uuid;
        JsonObject data;
        long timestamp;
        try{
            uuid = UUID.fromString(json.get("uuid").getAsString());
            data = json.getAsJsonObject("data");
            timestamp = json.get("timestamp").getAsLong();
        }
        catch(Exception e){
            return null;
        }

        return new EntityDeathLogEntry(uuid, data, timestamp);
    }
}
