package ch.swisssmp.observatory;

import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.function.Consumer;

public class ObservatoryEntries {

    private static HashSet<Observatory> observatories = new HashSet();

    public static Observatory blockinObservatoryArea(Block block) { //TODO should probably return the observatory or city object
        for (Observatory observatory : observatories) {
            Location location = block.getLocation();
            Vector locationVector = location.toVector();
            if (observatory.getWorld() == location.getWorld()) {
                if (locationVector.isInSphere(observatory.getCenter().toVector(), Observatory.OBSERVATORY_RADIUS))
                    return observatory;
            }
        }
        return null;
    }

    public static void reload(Consumer<String> sendResult) {
        HTTPRequest request = DataSource.getResponse(ObservatoryPlugin.getInstance(), "observatories.php");
        request.onFinish(() -> {
            JsonObject jsonResponse = request.getJsonResponse();
            if (jsonResponse == null || jsonResponse.isJsonNull()) {
                if (sendResult != null) {
                    sendResult.accept("Aktualisierung der Observatorien fehlgeschlagen.");
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
        JsonArray rawObservatories = monumentData.getAsJsonArray("observatories");
        if (rawObservatories == null ) {
            if (sendResult != null) {
                sendResult.accept("Keine Observatorien gefunden, Observatorien nicht aktualisiert");
            }
            return;
        }
        observatories.clear();
        for (JsonElement rawObservatory : rawObservatories) {
            if (rawObservatory.isJsonObject()) {
                observatories.add(loadObservatory(rawObservatory.getAsJsonObject()));
            }
        }
        if (sendResult != null) {
            sendResult.accept(observatories.size() + " Observatorien geladen.");
        }
    }

    private static Observatory loadObservatory(JsonObject rawObservatory) {
        World world = Bukkit.getWorld(JsonUtil.getString("world", rawObservatory));

        Observatory observatory = new Observatory(
                world,
                JsonUtil.getInt("id", rawObservatory),
                JsonUtil.getInt("city_id", rawObservatory),
                JsonUtil.getLocation("center", world, rawObservatory));
        return observatory;
    }
}
