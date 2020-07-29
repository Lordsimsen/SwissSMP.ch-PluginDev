package ch.swisssmp.city;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import ch.swisssmp.utils.*;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Citizenship {
    private final UUID cityId;
    private final PlayerData playerData;
    private CitizenRank rank;
    private final UUID parent;
    private String role;

    protected Citizenship(UUID cityId, PlayerData playerData, CitizenRank rank, UUID parent, String role) {
        this.cityId = cityId;
        this.playerData = playerData;
        this.rank = rank;
        this.parent = parent;
        this.role = role;
    }

    public UUID getCityId() {
        return cityId;
    }

    public City getCity() {
        return CitySystem.getCity(cityId).orElse(null);
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public UUID getUniqueId() {
        return playerData.getUniqueId();
    }

    public String getDisplayName() {
        return playerData.getDisplayName();
    }

    public String getName() {
        return playerData.getName();
    }

    public ItemStack getHead() {
        return playerData.getHead();
    }

    public CitizenRank getRank() {
        return rank;
    }

    public void setRank(CitizenRank rank) {
        this.rank = rank;
    }

    public UUID getParent() {
        return parent;
    }

    public String getRole() {
        return role;
    }

    protected void setRole(String role) {
        String filteredRole = role != null ? role.replaceAll("(§[a-z0-9])", "") : null;
        if (filteredRole.toLowerCase().startsWith("kein titel")) {
            this.role = "";
        } else if (filteredRole.toLowerCase().equals("bürgermeister") || filteredRole.toLowerCase().equals("buergermeister")) {
            this.role = "Bürgermeister";
        } else {
            this.role = filteredRole;
        }
    }

    public void announceRoleChange(Player responsible, String previous) {
        UUID playerUid = getUniqueId();
        String actor = !responsible.getUniqueId().equals(playerUid) ? responsible.getDisplayName() : null;
        Player citizenPlayer = Bukkit.getPlayer(playerUid);
        if (citizenPlayer != null) {
            if (!role.isEmpty()) {
                if (actor == null) actor = ChatColor.GREEN + "Du hast";
                else actor += ChatColor.GREEN + " hat";
                SwissSMPler.get(citizenPlayer).sendMessage(CitySystemPlugin.getPrefix() + actor + ChatColor.GREEN + " dir den Titel " + role + " verliehen!");
            } else if (!previous.isEmpty()) {
                if (actor == null) actor = ChatColor.GRAY + "Du hast";
                else actor += ChatColor.GRAY + " hat";
                SwissSMPler.get(citizenPlayer).sendMessage(CitySystemPlugin.getPrefix() + actor + ChatColor.GRAY + " deinen Titel " + previous + " entfernt.");
            }
        }
    }

    public void announceCitizenshipAwarded(Player responsible){
        City city = getCity();
        SwissSMPler.get(playerData.getUniqueId()).sendMessage(CitySystemPlugin.getPrefix() + responsible.getDisplayName() + ChatColor.GREEN + " hat dich in " + city.getName() + " aufgenommen!");
    }

    public void announceCitizenshipRevoked(Player responsible){
        UUID playerUid = getUniqueId();
        City city = getCity();
        boolean isOwner = responsible.getUniqueId().equals(playerUid);
        if(isOwner){
            SwissSMPler.get(playerUid).sendMessage(CitySystemPlugin.getPrefix()+ChatColor.GRAY+city.getName()+" verlassen.");
        }
        else{
            SwissSMPler.get(playerUid).sendMessage(CitySystemPlugin.getPrefix()+ChatColor.GRAY+"Du wurdest aus "+city.getName()+" ausgeschlossen.");
        }
    }

    public void save() {
        save(null);
    }

    public void save(Consumer<Boolean> callback) {
        HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), CitySystemUrl.SAVE_CITIZENSHIP, new String[]{
                "player=" + playerData.getUniqueId(),
                "city="+cityId,
                "rank=" + rank,
                "role=" + URLEncoder.encode(role),
                "parent_uuid=" + parent
        });
        request.onFinish(() -> {
            JsonObject json = request.getJsonResponse();
            boolean success = json != null && JsonUtil.getBool("success", json);
            String message = json != null ? JsonUtil.getString("message", json) : null;
            if (message != null) {
                Bukkit.getLogger().info(CitySystemPlugin.getPrefix() + " " + message);
            }

            if (callback != null) callback.accept(success);
        });
    }

    public void remove() {
        remove(null);
    }

    public void remove(Consumer<Boolean> callback) {
        HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), CitySystemUrl.REMOVE_CITIZENSHIP, new String[]{
                "player=" + playerData.getUniqueId(),
				"city="+cityId
        });
        request.onFinish(()->{
            JsonObject json = request.getJsonResponse();
            boolean success = json!=null && JsonUtil.getBool("success", json);
            String message = json!=null ? JsonUtil.getString("message", json) : null;
            if(message!=null) Bukkit.getLogger().info(CitySystemPlugin.getPrefix()+" "+message);
            if(callback!=null) callback.accept(success);
        });
    }

    private JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("player", playerData.toJson());
        JsonUtil.set("rank", rank.toString(), json);
        JsonUtil.set("role", role, json);
        JsonUtil.set("parent_uuid", parent, json);
        return json;
    }

    public static Optional<Citizenship> get(JsonObject json) {
        UUID cityId = JsonUtil.getUUID("city_id", json);
        PlayerData playerData = PlayerData.get(json).orElse(null);
        if (cityId == null || playerData == null) return Optional.empty();

        CitizenRank rank = CitizenRank.get(JsonUtil.getString("rank", json));
        String role = JsonUtil.getString("role", json);
        UUID parent;
        try {
            parent = JsonUtil.getUUID("parent_uuid", json);
        } catch (Exception e) {
            parent = null;
        }
        return Optional.of(new Citizenship(cityId, playerData, rank, parent, role));
    }
}
