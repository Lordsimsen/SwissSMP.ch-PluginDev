package ch.swisssmp.city;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import ch.swisssmp.utils.JsonUtil;

import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

public class Addon {

    private final String addonId;
    private final UUID cityId;

    private AddonState state;
    private AddonStateReason reason;

    private BlockVector origin;
    private String worldName;
    private boolean guideActive;

    private JsonObject data;

    public Addon(String addonId, UUID cityId) {
        this.cityId = cityId;
        this.addonId = addonId;
    }

    public String getName() {
        AddonType type = getType();
        return type != null ? type.getName() : "Unbekanntes Addon";
    }

    public String getAddonId() {
        return addonId;
    }

    public AddonType getType() {
        City city = CitySystem.getCity(cityId).orElse(null);
        Techtree techtree = city != null ? CitySystem.getTechtree(city.getTechtreeId()).orElse(null) : null;
        return techtree != null ? techtree.getAddonType(addonId).orElse(null) : null;
    }

    public UUID getCityId() {
        return cityId;
    }

    public City getCity() {
        return CitySystem.getCity(cityId).orElse(null);
    }

    public AddonState getState() {
        return this.state;
    }

    public void setAddonState(AddonState state) {
        setAddonState(state, AddonStateReason.NONE);
    }

    public void setAddonState(AddonState state, AddonStateReason reason) {
        this.state = state;
        this.reason = reason;
    }

    public void setAddonStateReason(AddonStateReason reason) {
        this.reason = reason;
    }

    public AddonStateReason getStateReason() {
        return this.reason;
    }

    public String getStateReasonMessage() {
        City city = getCity();
        Techtree techtree = city.getTechtree();
        AddonType type = techtree.getAddonType(addonId).orElse(null);
        switch (reason) {
            case CITY_LEVEL: {
                CityLevel level = techtree.getLevel(type.getCityLevel());
                return ChatColor.GRAY + "Benötigt Stadtstufe\n" + ChatColor.RED + level.getName();
            }
            case REQUIRED_ADDONS: {
                Bukkit.getLogger().info(this.addonId+" requires addons "+String.join(", ", type.getRequiredAddons()));
                Collection<String> missingAddons = Arrays.stream(type.getRequiredAddons())
                        .map(a -> techtree.getAddonType(a).orElse(null))
                        .filter(Objects::nonNull)
                        .map(t -> new AbstractMap.SimpleEntry<>(t, city.getAddon(t).orElse(null)))
                        .filter(e -> e.getValue() == null || (e.getValue().getState() != AddonState.ACCEPTED && e.getValue().getState() != AddonState.ACTIVATED))
                        .map(e -> e.getKey().getName())
                        .collect(Collectors.toList());
                return ChatColor.GRAY + "Benötigt Addon" + (missingAddons.size() > 1
                        ? "s\n" + missingAddons.stream().map(a->"- " + ChatColor.RED + a).collect(Collectors.joining("\n"))
                        : "\n"+ChatColor.RED+missingAddons.stream().findFirst().orElse("???"));
            }
            default:
                return null;
        }
    }

    public boolean unlock() {
        AddonType type = getType();
        if (type == null) return false;
        boolean autoActivate = type.getAutoActivate();
        this.state = autoActivate ? AddonState.ACTIVATED : AddonState.ACCEPTED;
        this.reason = AddonStateReason.NONE;
        return true;
    }

    public void announceUnlock(Player responsible) {
        City city = getCity();
        if (city != null) {
            city.broadcast(CitySystemPlugin.getPrefix() + " " + ChatColor.GREEN + city.getName() + " hat das Addon " + getType().getName() + " aktiviert!");
        }
    }

    public void setOrigin(Block block) {
        this.origin = block != null ? new BlockVector(block.getX(), block.getY(), block.getZ()) : null;
        this.worldName = block != null ? block.getWorld().getName() : null;
    }

    public void setOrigin(String worldName, BlockVector vector) {
        this.origin = origin;
        this.worldName = worldName;
    }

    public BlockVector getOrigin() {
        return origin;
    }

    public String getWorldName() {
        return worldName;
    }

    public boolean hasGuideActive() {
        return guideActive;
    }

    public void setGuideActive(boolean active) {
        this.guideActive = active;
    }

    public JsonObject getData() {
        return data;
    }

    public void save() {
        save(null);
    }

    public void save(Consumer<Boolean> callback) {
        HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), CitySystemUrl.SAVE_ADDON, new String[]{
                "addon_id=" + URLEncoder.encode(addonId),
                "city_id=" + cityId,
                "state=" + state,
                "state_reason=" + reason,
                "x=" + (origin != null ? origin.getBlockX() : 0),
                "y=" + (origin != null ? origin.getBlockY() : 0),
                "z=" + (origin != null ? origin.getBlockZ() : 0),
                "world=" + (worldName != null ? worldName : ""),
                "guide_active=" + (guideActive ? 1 : 0),
                "data=" + (data != null ? URLEncoder.encode(data.toString()) : "[]")
        });
        request.onFinish(() -> {
            JsonObject json = request.getJsonResponse();
            boolean success = (json != null && json.has("success") && JsonUtil.getBool("success", json));
            if (json != null && json.has("message")) {
                Bukkit.getLogger().info(CitySystemPlugin.getPrefix() + " " + JsonUtil.getString("message", json));
            }

            if (callback != null) callback.accept(success);
        });
    }

    public void reload() {
        reload(null);
    }

    public void reload(Consumer<Boolean> callback) {
        HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), CitySystemUrl.GET_ADDON, new String[]{
                "addon_id=" + URLEncoder.encode(addonId),
                "city_id=" + cityId
        });
        request.onFinish(() -> {
            JsonObject json = request.getJsonResponse();
            boolean success = json != null && JsonUtil.getBool("success", json);
            String message = json != null ? JsonUtil.getString("message", json) : null;
            if (message != null) {
                Bukkit.getLogger().info(CitySystemPlugin.getPrefix() + " " + message);
            }
            if (success) {
                loadData(json.getAsJsonObject("addon"));
            }
            if (callback != null) callback.accept(success);
        });
    }

    private void loadData(JsonObject json) {
        state = AddonState.get(JsonUtil.getString("state", json));
        reason = AddonStateReason.of(JsonUtil.getString("state_reason", json));
        origin = new BlockVector(JsonUtil.getInt("x", json), JsonUtil.getInt("y", json), JsonUtil.getInt("z", json));
        worldName = JsonUtil.getString("world", json);
        guideActive = JsonUtil.getBool("guide_active", json);
        data = json.has("data") && json.get("data").isJsonObject() ? json.getAsJsonObject("data") : new JsonObject();
    }

    protected static Optional<Addon> load(JsonObject json) {
        String addonId = JsonUtil.getString("addon_id", json);
        UUID cityId = JsonUtil.getUUID("city_id", json);
        if (addonId == null || cityId == null) return Optional.empty();

        Addon result = new Addon(addonId, cityId);
        result.loadData(json);
        return Optional.of(result);
    }
}
