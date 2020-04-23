package ch.swisssmp.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Color;
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

    public static String getString(String key, JsonObject json){
        JsonElement element = json.has(key) ? json.get(key) : null;
        return element!=null && element.isJsonPrimitive() ? element.getAsString() : null;
    }

    public static float getFloat(String key, JsonObject json){
        JsonElement element = json.has(key) ? json.get(key) : null;
        return element!=null && element.isJsonPrimitive() ? element.getAsFloat() : 0;
    }

    public static double getDouble(String key, JsonObject json){
        JsonElement element = json.has(key) ? json.get(key) : null;
        return element!=null && element.isJsonPrimitive() ? element.getAsDouble() : 0;
    }

    public static int getInt(String key, JsonObject json){
        JsonElement element = json.has(key) ? json.get(key) : null;
        return element!=null && element.isJsonPrimitive() ? element.getAsInt() : 0;
    }

    public static boolean getBool(String key, JsonObject json){
        JsonElement element = json.has(key) ? json.get(key) : null;
        return element!=null && element.isJsonPrimitive() && element.getAsBoolean();
    }

    public static Color getColor(String key, JsonObject json){
        return Color.fromRGB(getInt(key,json));
    }

    public static Block getBlock(String key, World world, JsonObject json){
        JsonElement locationElement = json.has(key) ? json.get(key) : null;
        if(locationElement==null || !locationElement.isJsonObject()) return null;
        JsonObject locationData = locationElement.getAsJsonObject();
        int x = getInt("x", locationData);
        int y = getInt("y", locationData);
        int z = getInt("z", locationData);
        return world.getBlockAt(x,y,z);
    }

    public static Location getLocation(String key, World world, JsonObject json){
        JsonElement locationElement = json.has(key) ? json.get(key) : null;
        if(locationElement==null || !locationElement.isJsonObject()) return null;
        JsonObject locationData = locationElement.getAsJsonObject();
        double x = getDouble("x", locationData);
        double y = getDouble("y", locationData);
        double z = getDouble("z", locationData);
        if(locationData.has("pitch") && locationData.has("yaw")){
            float pitch = getFloat("pitch", locationData);
            float yaw = getFloat("yaw", locationData);
            return new Location(world, x, y, z, yaw, pitch);
        }

        return new Location(world, x, y, z);
    }
}
