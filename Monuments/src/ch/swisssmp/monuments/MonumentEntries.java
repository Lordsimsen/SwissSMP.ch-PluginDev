package ch.swisssmp.monuments;

import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import org.bukkit.Location;
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
                Bukkit.getLogger().info(MonumentsPlugin.getPrefix() + " Fehler beim Laden der Monumente. Bitte API prüfen.");
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
        JsonArray rawMonuments = monumentData.getAsJsonArray("monuments");
        if (rawMonuments == null) {
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
            sendResult.accept("Monumente aktualisiert.");
        }
    }

    private static Monument loadMonument(JsonObject rawMonument) {

        JsonObject rawMonumentAsJsonObject = rawMonument.getAsJsonObject();
        Monument monument = new Monument(
                JsonUtil.getString("world", rawMonumentAsJsonObject),
                JsonUtil.getInt("id", rawMonumentAsJsonObject),
                JsonUtil.getString("addon_id", rawMonumentAsJsonObject),
                JsonUtil.getInt("x", rawMonumentAsJsonObject),
                JsonUtil.getInt("y", rawMonumentAsJsonObject),
                JsonUtil.getInt("z", rawMonumentAsJsonObject),
                JsonUtil.getInt("radius", rawMonumentAsJsonObject));
        return monument;
    }

    public static boolean blockInAnyMonumentArea(Block block) {
        for (Monument monument : monuments) {
            Location location = block.getLocation();
            Vector locationVector = location.toVector();
            if (monument.world == location.getWorld().getName()) {
                if (locationVector.isInSphere(monument.locationVector, monument.radius))
                    return true;
            }
        }
        return false;
    }

    public static class Monument {
        String world;
        int id;
        String addonId;
        Vector locationVector;
        int radius;

        public Monument(String world, int id, String addonId, int x, int y, int z, int radius) {
            this.world = world;
            this.id = id;
            this.addonId = addonId;
            this.locationVector = new Vector(x, y, z);
            this.radius = radius;
        }
    }
}
