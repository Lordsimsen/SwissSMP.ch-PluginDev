package ch.swisssmp.city;

import java.util.*;
import java.util.function.Consumer;

import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.StringUtils;

class Cities {
    private static final HashSet<City> cities = new HashSet<>();

    protected static Optional<City> findCity(String key) {
        UUID cityUid;
        try {
            cityUid = UUID.fromString(key);
            return getCity(cityUid);
        } catch (Exception ignored) { }
        String lowerCaseKey = key.toLowerCase();
        return cities.stream().filter(c -> c.getName().equalsIgnoreCase(lowerCaseKey) || c.getName().toLowerCase().startsWith(lowerCaseKey)).findAny();
    }

    protected static void add(City city) {
        cities.add(city);
    }

    protected static void remove(String key) {
        City city = findCity(key).orElse(null);
        if (city == null) return;
        remove(city);
    }

    protected static void remove(City city) {
        cities.remove(city);
    }

    protected static Optional<City> load(JsonObject json) {
        City result = City.load(json).orElse(null);
        if (result == null) return Optional.empty();
        cities.add(result);
        return Optional.of(result);
    }

    protected static void loadAll() {
        loadAll(null);
    }

    protected static void loadAll(Consumer<Boolean> callback) {
        HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), CitySystemUrl.GET_CITIES);
        request.onFinish(() -> {
            loadAll(request.getJsonResponse(), callback);
        });
    }

    private static void loadAll(JsonObject json, Consumer<Boolean> callback) {
        unloadAll();
        boolean success = json != null && JsonUtil.getBool("success", json);
        if (success) {
            JsonArray citiesArray = json.getAsJsonArray("cities");
            for (JsonElement element : citiesArray) {
                JsonObject citySection = element.getAsJsonObject();
                City city = City.load(citySection).orElse(null);
                if (city == null) {
                    Bukkit.getLogger().warning(CitySystemPlugin.getPrefix() + " Konnte Stadt nicht laden:\n" + element.toString());
                    continue;
                }
                cities.add(city);
            }
        }

        if (callback != null) callback.accept(success);
    }

    protected static void unloadAll() {
        cities.clear();
    }

    protected static Optional<City> getCity(UUID cityId) {
        return cities.stream().filter(c -> c.getUniqueId().equals(cityId)).findAny();
    }

    @Deprecated
    protected static Optional<City> getCity(int legacyId) {
        return cities.stream().filter(c -> c.getId()==legacyId).findAny();
    }

    protected static Collection<City> getAll() {
        return Collections.unmodifiableCollection(cities);
    }
}
