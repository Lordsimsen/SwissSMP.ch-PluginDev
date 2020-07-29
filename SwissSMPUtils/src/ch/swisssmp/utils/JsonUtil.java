package ch.swisssmp.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonUtil {

    public static boolean save(File file, JsonObject json) {
        try {
            if (!file.getParentFile().isDirectory()) {
                if (!file.getParentFile().mkdirs()) {
                    return false;
                }
            }
        } catch (Exception e) {
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

    public static JsonObject parse(File file) {
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

    public static JsonObject parse(String s) {
        JsonParser parser = new JsonParser();
        try {
            JsonElement result = parser.parse(s);
            return result.isJsonObject() ? result.getAsJsonObject() : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static UUID getUUID(String key, JsonObject json) {
        JsonElement element = json.has(key) ? json.get(key) : null;
        String idString = element != null && element.isJsonPrimitive() ? element.getAsString() : null;
        try {
            return idString != null ? UUID.fromString(idString) : null;
        } catch (Exception e) {
            if(idString!=null && idString.length()>0){
                Bukkit.getLogger().warning("[JsonUtil] Konnte ungültige UUID nicht lesen: "+idString);
            }
            return null;
        }
    }

    public static void set(String key, UUID uuid, JsonObject json) {
        json.addProperty(key, uuid.toString());
    }

    public static String getString(String key, JsonObject json) {
        JsonElement element = json.has(key) ? json.get(key) : null;
        return element != null && element.isJsonPrimitive() ? element.getAsString() : null;
    }

    public static void set(String key, String value, JsonObject json) {
        json.addProperty(key, value);
    }

    public static float getFloat(String key, JsonObject json) {
        JsonElement element = json.has(key) ? json.get(key) : null;
        return element != null && element.isJsonPrimitive() ? element.getAsFloat() : 0;
    }

    public static void set(String key, float value, JsonObject json) {
        json.addProperty(key, value);
    }

    public static double getDouble(String key, JsonObject json) {
        JsonElement element = json.has(key) ? json.get(key) : null;
        return element != null && element.isJsonPrimitive() ? element.getAsDouble() : 0;
    }

    public static void set(String key, double value, JsonObject json) {
        json.addProperty(key, value);
    }

    public static int getInt(String key, JsonObject json) {
        JsonElement element = json.has(key) ? json.get(key) : null;
        return element != null && element.isJsonPrimitive() ? element.getAsInt() : 0;
    }

    public static void set(String key, int value, JsonObject json) {
        json.addProperty(key, value);
    }

    public static short getShort(String key, JsonObject json) {
        JsonElement element = json.has(key) ? json.get(key) : null;
        return element != null && element.isJsonPrimitive() ? element.getAsShort() : 0;
    }

    public static void set(String key, short value, JsonObject json) {
        json.addProperty(key, value);
    }

    public static boolean getBool(String key, JsonObject json) {
        JsonElement element = json.has(key) ? json.get(key) : null;
        return element != null && element.isJsonPrimitive() && element.getAsBoolean();
    }

    public static void set(String key, boolean value, JsonObject json) {
        json.addProperty(key, value);
    }

    public static List<String> getStringList(String key, JsonObject json){
        if(!json.has(key)) return Collections.emptyList();
        JsonElement element = json.get(key);
        if(element.isJsonArray()) getStringList(element.getAsJsonArray());
        if(element.isJsonObject()) return element.getAsJsonObject().entrySet().stream().map(e->e.getValue().toString()).collect(Collectors.toList());
        String stringValue = element.toString();
        if(stringValue.equals("[]") || stringValue.equals("{}")) return Collections.emptyList();
        return Collections.singletonList(element.toString());
    }

    public static List<String> getStringList(JsonArray json){
        List<String> result = new ArrayList<>();
        for(JsonElement element : json){
            result.add(element.getAsString());
        }
        return result;
    }

    public static void set(String key, List<String> value, JsonObject json){
        JsonArray valueArray = new JsonArray();
        for(String element : value){
            valueArray.add(element);
        }
        json.add(key, valueArray);
    }

    public static NamespacedKey getKey(String key, JsonObject json) {
        return json.has(key) ? getKey(json.get(key)) : null;
    }

    public static NamespacedKey getKey(JsonElement json) {
        String keyString = json.getAsString();
        String namespace = keyString.contains(":") ? keyString.substring(0, keyString.indexOf(":")) : "minecraft";
        String key = keyString.contains(":") ? keyString.substring(namespace.length() + 1) : keyString;
        //noinspection deprecation
        return json != null ? new NamespacedKey(namespace, key) : null;
    }

    public static void set(String key, NamespacedKey value, JsonObject json) {
        json.addProperty(key, value.toString());
    }

    public static Material getMaterial(String key, JsonObject json) {
        return json.has(key) ? getMaterial(json.get(key)) : null;
    }

    public static Material getMaterial(JsonElement json) {
        Material result;
        try {
            result = json != null ? Material.valueOf(json.getAsString().toUpperCase()) : null;
        } catch (Exception ignored) {
            result = null;
        }
        if (result != null || json == null) return result;
        NamespacedKey key = getKey(json);
        return MaterialUtil.getMaterial(key).orElse(null);
    }

    public static void set(String key, Material value, JsonObject json) {
        json.addProperty(key, value.toString());
    }

    public static Enchantment getEnchantment(String key, JsonObject json) {
        return json.has(key) ? getEnchantment(json.get(key)) : null;
    }

    public static Enchantment getEnchantment(JsonElement json) {
        if(json==null) return null;
        String value = json.getAsString();
        Enchantment[] values = Enchantment.values();
        for (Enchantment e : values) {
            if (e.toString().equalsIgnoreCase(value)) return e;
        }

        return Enchantment.getByKey(getKey(json));
    }

    public static void set(String key, Enchantment value, JsonObject json) {
        json.addProperty(key, value.toString());
    }

    public static Color getColor(String key, JsonObject json) {
        return Color.fromRGB(getInt(key, json));
    }

    public static void set(String key, Color value, JsonObject json) {
        json.addProperty(key, value.asRGB());
    }

    public static EnchantmentData getEnchantmentData(String key, JsonObject json) {
        return json.has(key) ? getEnchantmentData(json.getAsJsonObject(key)) : null;
    }

    public static EnchantmentData getEnchantmentData(JsonObject json) {
        Enchantment enchantment = JsonUtil.getEnchantment("enchantment", json);
        if (enchantment == null) return null;
        int level = JsonUtil.getInt("level", json);
        boolean ignoreLevelRestriction = JsonUtil.getBool("ignore_level_restriction", json);
        if (!ignoreLevelRestriction) level = Math.min(level, enchantment.getMaxLevel());
        return new EnchantmentData(enchantment, level, ignoreLevelRestriction);
    }

    public static void set(String key, EnchantmentData block, JsonObject json) {
        json.add(key, toJsonObject(block));
    }

    public static JsonObject toJsonObject(EnchantmentData data) {
        JsonObject json = new JsonObject();
        json.addProperty("enchantment", data.getEnchantment().toString());
        json.addProperty("level", data.getLevel());
        if(data.getIgnoreLevelRestriction()) json.addProperty("ignore_level_restriction", data.getIgnoreLevelRestriction());
        return json;
    }

    public static Block getBlock(String key, World world, JsonObject json) {
        JsonElement locationElement = json.has(key) ? json.get(key) : null;
        if (locationElement == null || !locationElement.isJsonObject()) return null;
        JsonObject locationData = locationElement.getAsJsonObject();
        int x = getInt("x", locationData);
        int y = getInt("y", locationData);
        int z = getInt("z", locationData);
        return world.getBlockAt(x, y, z);
    }

    public static void set(String key, Block block, JsonObject json) {
        json.add(key, toJsonObject(block));
    }

    public static JsonObject toJsonObject(Block b) {
        JsonObject json = new JsonObject();
        json.addProperty("x", b.getX());
        json.addProperty("y", b.getY());
        json.addProperty("z", b.getZ());
        return json;
    }

    public static Location getLocation(World world, JsonObject json) {
        if (json == null || !json.isJsonObject()) return null;
        double x = getDouble("x", json);
        double y = getDouble("y", json);
        double z = getDouble("z", json);
        if (json.has("pitch") && json.has("yaw")) {
            float pitch = getFloat("pitch", json);
            float yaw = getFloat("yaw", json);
            return new Location(world, x, y, z, yaw, pitch);
        }

        return new Location(world, x, y, z);
    }

    public static Location getLocation(String key, World world, JsonObject json) {
        JsonElement locationElement = json.has(key) ? json.get(key) : null;
        if (locationElement == null || !locationElement.isJsonObject()) return null;
        JsonObject locationData = locationElement.getAsJsonObject();
        return getLocation(world, locationData);
    }

    public static void set(String key, Location location, JsonObject json) {
        json.add(key, toJsonObject(location));
    }

    public static JsonObject toJsonObject(Location l) {
        JsonObject json = new JsonObject();
        json.addProperty("x", l.getX());
        json.addProperty("y", l.getY());
        json.addProperty("z", l.getZ());
        json.addProperty("yaw", l.getYaw());
        json.addProperty("pitch", l.getPitch());
        return json;
    }

    public static Position getPosition(JsonObject json) {
        if (json == null || !json.isJsonObject()) return null;
        double x = getDouble("x", json);
        double y = getDouble("y", json);
        double z = getDouble("z", json);
        if (json.has("pitch") && json.has("yaw")) {
            float pitch = getFloat("pitch", json);
            float yaw = getFloat("yaw", json);
            return new Position(x, y, z, yaw, pitch);
        }

        return new Position(x, y, z);
    }

    public static Position getPosition(String key, JsonObject json) {
        JsonElement positionElement = json.has(key) ? json.get(key) : null;
        if (positionElement == null || !positionElement.isJsonObject()) return null;
        JsonObject locationData = positionElement.getAsJsonObject();
        return getPosition(locationData);
    }

    public static void set(String key, Position position, JsonObject json) {
        json.add(key, toJsonObject(position));
    }

    public static JsonObject toJsonObject(Position p) {
        JsonObject json = new JsonObject();
        json.addProperty("x", p.getX());
        json.addProperty("y", p.getY());
        json.addProperty("z", p.getZ());
        json.addProperty("yaw", p.getYaw());
        json.addProperty("pitch", p.getPitch());
        return json;
    }

    public static Vector getVector(JsonObject json) {
        if (json == null || !json.isJsonObject()) return null;
        double x = getDouble("x", json);
        double y = getDouble("y", json);
        double z = getDouble("z", json);

        return new Vector(x, y, z);
    }

    public static Vector getVector(String key, JsonObject json) {
        JsonElement vectorElement = json.has(key) ? json.get(key) : null;
        if (vectorElement == null || !vectorElement.isJsonObject()) return null;
        JsonObject locationData = vectorElement.getAsJsonObject();
        return getVector(locationData);
    }

    public static void set(String key, Vector vector, JsonObject json) {
        json.add(key, toJsonObject(vector));
    }

    public static JsonObject toJsonObject(Vector v) {
        JsonObject json = new JsonObject();
        json.addProperty("x", v.getX());
        json.addProperty("y", v.getY());
        json.addProperty("z", v.getZ());
        return json;
    }

    public static BlockVector getBlockVector(JsonObject json) {
        if (json == null || !json.isJsonObject()) return null;
        int x = getInt("x", json);
        int y = getInt("y", json);
        int z = getInt("z", json);

        return new BlockVector(x, y, z);
    }

    public static BlockVector getBlockVector(String key, JsonObject json) {
        JsonElement element = json.has(key) ? json.get(key) : null;
        if (element == null || !element.isJsonObject()) return null;
        JsonObject locationData = element.getAsJsonObject();
        return getBlockVector(locationData);
    }

    public static void set(String key, BlockVector vector, JsonObject json) {
        json.add(key, toJsonObject(vector));
    }

    public static JsonObject toJsonObject(BlockVector v) {
        JsonObject json = new JsonObject();
        json.addProperty("x", v.getBlockX());
        json.addProperty("y", v.getBlockY());
        json.addProperty("z", v.getBlockZ());
        return json;
    }

    public static ItemStack getItemStack(String key, JsonObject json) {
        JsonElement element = json.has(key) ? json.get(key) : null;
        if (element == null || !element.isJsonPrimitive()) return null;
        return ItemUtil.deserialize(element.getAsString());
    }

    public static void set(String key, ItemStack itemStack, JsonObject json) {
        if (itemStack == null) {
            if (json.has(key)) json.remove(key);
            return;
        }
        json.addProperty(key, ItemUtil.serialize(itemStack));
    }
}
