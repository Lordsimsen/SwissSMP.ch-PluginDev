package ch.swisssmp.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.io.File;
import java.io.FileReader;

public class JsonUtil {
    public static JsonObject parse(File file){
        JsonParser parser = new JsonParser();
        try {
            FileReader reader = new FileReader(file);
            JsonElement result = parser.parse(reader);
            return result.isJsonObject() ? result.getAsJsonObject() : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static JsonObject parse(String s){
        JsonParser parser = new JsonParser();
        try{
            JsonElement result = parser.parse(s);
            return result.isJsonObject() ? result.getAsJsonObject() : null;
        }
        catch(Exception e){
            return null;
        }
    }

    public static Block getBlock(String key, World world, JsonObject json){
        JsonElement locationElement = json.has(key) ? json.get(key) : null;
        if(locationElement==null || !locationElement.isJsonObject()) return null;
        JsonObject locationData = locationElement.getAsJsonObject();
        int x = locationData.has("x") ? locationData.get("x").getAsInt() : 0;
        int y = locationData.has("y") ? locationData.get("y").getAsInt() : 0;
        int z = locationData.has("z") ? locationData.get("z").getAsInt() : 0;
        return world.getBlockAt(x,y,z);
    }

    public static Location getLocation(String key, World world, JsonObject json){
        JsonElement locationElement = json.has(key) ? json.get(key) : null;
        if(locationElement==null || !locationElement.isJsonObject()) return null;
        JsonObject locationData = locationElement.getAsJsonObject();
        double x = locationData.has("x") ? locationData.get("x").getAsDouble() : 0;
        double y = locationData.has("y") ? locationData.get("y").getAsDouble() : 0;
        double z = locationData.has("z") ? locationData.get("z").getAsDouble() : 0;
        if(locationData.has("pitch") && locationData.has("yaw")){
            float pitch = locationData.get("pitch").getAsFloat();
            float yaw = locationData.get("yaw").getAsFloat();
            return new Location(world, x, y, z, yaw, pitch);
        }

        return new Location(world, x, y, z);
    }
}
