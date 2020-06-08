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
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class JsonUtil {

    public static boolean save(File file, JsonObject json){
        try{
            if(!file.getParentFile().isDirectory()){
                if(!file.getParentFile().mkdirs()){
                    return false;
                }
            }
        }
        catch(Exception e){
            return false;
        }
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(json.toString());
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static JsonObject parse(File file){
        JsonParser parser = new JsonParser();
        try {
            FileReader reader = new FileReader(file);
            JsonElement result = parser.parse(reader);
            reader.close();
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

    public static UUID getUUID(String key, JsonObject json){
        JsonElement element = json.has(key) ? json.get(key) : null;
        String idString = element!=null && element.isJsonPrimitive() ? element.getAsString() : null;
        try{
            return idString!=null ? UUID.fromString(idString) : null;
        }
        catch(Exception e){
            return null;
        }
    }

    public static void set(String key, UUID uuid, JsonObject json){
        json.addProperty(key, uuid.toString());
    }

    public static String getString(String key, JsonObject json){
        JsonElement element = json.has(key) ? json.get(key) : null;
        return element!=null && element.isJsonPrimitive() ? element.getAsString() : null;
    }

    public static void set(String key, String value, JsonObject json){
        json.addProperty(key, value);
    }

    public static float getFloat(String key, JsonObject json){
        JsonElement element = json.has(key) ? json.get(key) : null;
        return element!=null && element.isJsonPrimitive() ? element.getAsFloat() : 0;
    }

    public static void set(String key, float value, JsonObject json){
        json.addProperty(key, value);
    }

    public static double getDouble(String key, JsonObject json){
        JsonElement element = json.has(key) ? json.get(key) : null;
        return element!=null && element.isJsonPrimitive() ? element.getAsDouble() : 0;
    }

    public static void set(String key, double value, JsonObject json){
        json.addProperty(key, value);
    }

    public static int getInt(String key, JsonObject json){
        JsonElement element = json.has(key) ? json.get(key) : null;
        return element!=null && element.isJsonPrimitive() ? element.getAsInt() : 0;
    }

    public static void set(String key, int value, JsonObject json){
        json.addProperty(key, value);
    }

    public static boolean getBool(String key, JsonObject json){
        JsonElement element = json.has(key) ? json.get(key) : null;
        return element!=null && element.isJsonPrimitive() && element.getAsBoolean();
    }

    public static void set(String key, boolean value, JsonObject json){
        json.addProperty(key, value);
    }

    public static Color getColor(String key, JsonObject json){
        return Color.fromRGB(getInt(key,json));
    }

    public static void set(String key, Color value, JsonObject json){
        json.addProperty(key, value.asRGB());
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

    public static void set(String key, Block block, JsonObject json){
        JsonObject locationData = new JsonObject();
        locationData.addProperty("x", block.getX());
        locationData.addProperty("y", block.getY());
        locationData.addProperty("z", block.getZ());

        json.add(key, locationData);
    }

    public static Location getLocation(World world, JsonObject json){
        if(json==null || !json.isJsonObject()) return null;
        double x = getDouble("x", json);
        double y = getDouble("y", json);
        double z = getDouble("z", json);
        if(json.has("pitch") && json.has("yaw")){
            float pitch = getFloat("pitch", json);
            float yaw = getFloat("yaw", json);
            return new Location(world, x, y, z, yaw, pitch);
        }

        return new Location(world, x, y, z);
    }

    public static Location getLocation(String key, World world, JsonObject json){
        JsonElement locationElement = json.has(key) ? json.get(key) : null;
        if(locationElement==null || !locationElement.isJsonObject()) return null;
        JsonObject locationData = locationElement.getAsJsonObject();
        return getLocation(world, locationData);
    }

    public static void set(String key, Location location, JsonObject json){
        JsonObject locationData = new JsonObject();
        locationData.addProperty("x", location.getX());
        locationData.addProperty("y", location.getY());
        locationData.addProperty("z", location.getZ());
        locationData.addProperty("pitch", location.getPitch());
        locationData.addProperty("yaw", location.getYaw());

        json.add(key, locationData);
    }
}
