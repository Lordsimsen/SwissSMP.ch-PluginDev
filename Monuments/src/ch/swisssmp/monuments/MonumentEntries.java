package ch.swisssmp.monuments;

import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.function.Consumer;

public class MonumentEntries {
    private static HashSet<Monument> monuments = new HashSet();

    public static void reload(Consumer<String> sendResult) {
        HTTPRequest request = DataSource.getResponse(MonumentsPlugin.getInstance(), "monuments.php");
        request.onFinish(() -> {
            JsonObject jsonResponse = request.getJsonResponse();
            if (jsonResponse == null || jsonResponse.isJsonNull()) {
                if (sendResult != null) {
                    sendResult.accept("Aktualisierung der Monumente fehlgeschlagen.");
                }
                return;
            }
            reload(sendResult, jsonResponse);
        });
    }

    public static void reload() {
        reload(null);
    }

    private static void reload(Consumer<String> sendResult, JsonObject monumentData) {
        // Bukkit.getLogger().info(monumentData.toString());
        JsonArray rawMonuments = monumentData.getAsJsonArray("monuments");
        if (rawMonuments == null ) {
            if (sendResult != null) {
                sendResult.accept("Keine Monumente gefunden, Monumente nicht aktualisiert");
            }
            return;
        }
        monuments.clear();
        for (JsonElement rawMonument : rawMonuments) {
            if (rawMonument.isJsonObject()) {
                monuments.add(loadMonument(rawMonument.getAsJsonObject()));
            }
        }
        if (sendResult != null) {
            sendResult.accept(monuments.size() + " Monumente geladen.");
        }
    }

    private static Monument loadMonument(JsonObject rawMonument) {
        World world = Bukkit.getWorld(JsonUtil.getString("world", rawMonument));

        Monument monument = new Monument(
                world,
                JsonUtil.getInt("id", rawMonument),
                JsonUtil.getString("addon_id", rawMonument),
                JsonUtil.getLocation("center", world, rawMonument),
                JsonUtil.getInt("radius", rawMonument));
        return monument;
    }

    public static boolean blockInAnyMonumentArea(Block block) {
        for (Monument monument : monuments) {
            Location location = block.getLocation();
            Vector locationVector = location.toVector();
            if (monument.world == location.getWorld()) {
                if (locationVector.isInSphere(monument.center.toVector(), monument.radius))
                    return true;
            }
        }
        return false;
    }

    public static class Monument {
        World world;
        int id;
        String addonId;
        Location center;
        int radius;

        public Monument(World world, int id, String addonId, Location center, int radius) {
            this.world = world;
            this.id = id;
            this.addonId = addonId;
            this.center = center;
            this.radius = radius;
        }
    }
}
