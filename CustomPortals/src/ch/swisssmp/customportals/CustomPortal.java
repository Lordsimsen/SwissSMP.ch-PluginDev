package ch.swisssmp.customportals;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.customportals.event.PlayerCustomPortalEvent;
import ch.swisssmp.editor.Removable;
import ch.swisssmp.utils.*;
import ch.swisssmp.utils.Random;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.*;

public class CustomPortal implements Removable {

    public static final String TOKEN_ENUM = "PORTAL";
    public static final String ID_PROPERTY = "CustomPortalId";

    // general info
    private final CustomPortalContainer container;
    private final UUID uid;
    private String regionId;
    private String name;

    // conditions
    private final Set<GameMode> allowedGameModes = new HashSet<>();
    private String travelPermission = null;
    private boolean active = true;

    // target location
    private String targetWorld = null;
    private Position fromPosition = null;
    private Position toPosition = null;
    private boolean useRelativeTeleport = true;

    // travel settings
    private String travelSound;
    private boolean keepVelocity = true;

    public CustomPortal(CustomPortalContainer container, UUID uid, String name) {
        this.container = container;
        this.uid = uid;
        this.name = name;
    }

    public CustomPortalContainer getContainer() {
        return container;
    }

    public UUID getUniqueId() {
        return uid;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<GameMode> getAllowedGameModes() {
        return allowedGameModes;
    }

    public void setAllowedGameModes(Collection<GameMode> gameModes) {
        allowedGameModes.clear();
        allowedGameModes.addAll(gameModes);
    }

    public String getTravelPermission() {
        return travelPermission;
    }

    public void setTravelPermission(String permission) {
        this.travelPermission = permission;
    }

    public boolean isPortalActive(){
        return active;
    }

    public void setPortalActive(boolean active){
        this.active = active;
    }

    public String getTargetWorld() {
        return targetWorld;
    }

    public void setTargetWorld(String world) {
        targetWorld = world;
    }

    public Position getToPosition() {
        return toPosition;
    }

    public void setToPosition(Position position) {
        toPosition = position;
    }

    public Position getFromPosition() {
        return fromPosition;
    }

    public void setFromPosition(Position position) {
        fromPosition = position;
    }

    public boolean getUseRelativeTeleportation() {
        return useRelativeTeleport;
    }

    public void setUseRelativeTeleportation(boolean value) {
        useRelativeTeleport = value;
    }

    public boolean getKeepVelocity() {
        return keepVelocity;
    }

    public void setKeepVelocity(boolean keepVelocity) {
        this.keepVelocity = keepVelocity;
    }

    public String getTravelSound() {
        return travelSound;
    }

    public void setTravelSound(String sound) {
        this.travelSound = sound;
    }

    public void setTravelSound(Sound sound) {
        this.setTravelSound(sound.toString());
    }

    public Optional<Location> createTeleportLocation(){
        return createTeleportLocation(null);
    }

    public Optional<Location> createTeleportLocation(Location from) {
        if (targetWorld == null || toPosition == null) return Optional.empty();
        World world = Bukkit.getWorld(targetWorld);
        if (world == null) return Optional.empty();
        Position p = toPosition;
        Location location = new Location(world, p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch());
        if(useRelativeTeleport && from!=null){
            Vector delta = from.subtract(fromPosition.getLocation(from.getWorld())).toVector();
            float yawDifference = location.getYaw() - fromPosition.getYaw();
            float pitchDifference = location.getPitch() - fromPosition.getPitch();
            Location temp = new Location(world, 0, 0, 0);
            temp.setDirection(delta);
            temp.setYaw(temp.getYaw()+yawDifference);
            temp.setPitch(temp.getPitch()+pitchDifference);
            delta = temp.getDirection().normalize().multiply(delta.length());
            location.add(delta);
            location.setYaw(from.getYaw()+yawDifference);
            location.setPitch(from.getPitch()+pitchDifference);
        }
        return Optional.of(location);
    }

    public boolean isSetupComplete() {
        return regionId != null && allowedGameModes.size() > 0 && targetWorld != null && toPosition != null && fromPosition != null;
    }

    public boolean canTravel(Player player) {
        return active && isSetupComplete() && allowedGameModes.contains(player.getGameMode()) && (travelPermission == null || player.hasPermission(travelPermission));
    }

    public boolean travel(Player player) {
        if(!active) return false;
        Location from = player.getLocation();
        Location to = createTeleportLocation(from).orElse(null);
        if (to == null) return false;
        PlayerCustomPortalEvent event = new PlayerCustomPortalEvent(player, this, from, to);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;
        Vector velocity = player.getVelocity();
        boolean result = player.teleport(to, PlayerTeleportEvent.TeleportCause.PLUGIN);
        if (!result) return false;
        if (travelSound != null) {
            player.playSound(to, travelSound, SoundCategory.BLOCKS, 0.1f, 1);
        }

        if (keepVelocity && velocity.lengthSquared()>0) {
            double speed = velocity.length();
            Position originPosition = getFromPosition();
            float yawDifference = to.getYaw() - originPosition.getYaw();
            float pitchDifference = to.getPitch() - originPosition.getPitch();
            Location temp = new Location(to.getWorld(), 0, 0, 0);
            temp.setDirection(velocity);
            temp.setYaw(temp.getYaw()+yawDifference);
            temp.setPitch(temp.getPitch()+pitchDifference);

            final Vector newVelocity = temp.getDirection().multiply(speed);
            Bukkit.getScheduler().runTaskLater(CustomPortalsPlugin.getInstance(), () -> {
                player.setVelocity(newVelocity);
            }, 1);
        }

        return true;
    }

    public void updateTokens(){
        String idString = uid.toString();
        ItemStack tokenStack = getItemStack();
        ItemMeta templateMeta = tokenStack.getItemMeta();
        ItemUtil.updateItemsGlobal((itemStack)->idString.equals(ItemUtil.getString(itemStack,ID_PROPERTY)), (itemStack)->itemStack.setItemMeta(templateMeta));
    }

    public List<String> getItemLore() {
        String world = getTargetWorld();
        List<String> lines = new ArrayList<>();
        if(!active) lines.add(ChatColor.RED+"Deaktiviert");
        if(fromPosition!=null){
            lines.add(ChatColor.GRAY+"Start:");
            lines.add(ChatColor.GRAY + container.getWorld().getName()+": " + fromPosition.getBlockX() + ", " + fromPosition.getBlockY() + ", " + fromPosition.getBlockZ());
        }
        if(world!=null && toPosition!=null){
            lines.add(ChatColor.GRAY+"Ziel:");
            lines.add(ChatColor.GRAY + world+": " + toPosition.getBlockX() + ", " + toPosition.getBlockY() + ", " + toPosition.getBlockZ());
        }
        return lines;
    }

    public ItemStack getItemStack() {
        CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder(TOKEN_ENUM);
        itemBuilder.setDisplayName(ChatColor.LIGHT_PURPLE + name);
        itemBuilder.setAmount(1);
        List<String> lore = getItemLore();
        itemBuilder.setLore(lore);
        ItemStack result = itemBuilder.build();
        ItemUtil.setString(result, ID_PROPERTY, uid.toString());
        return result;
    }

    protected JsonObject save() {
        JsonObject json = new JsonObject();

        JsonUtil.set("uid", uid, json);
        if (regionId != null) JsonUtil.set("region", regionId, json);
        JsonUtil.set("name", name, json);

        JsonArray allowedGameModesArray = new JsonArray();
        for (GameMode gameMode : getAllowedGameModes()) {
            allowedGameModesArray.add(gameMode.toString());
        }
        json.add("game_modes", allowedGameModesArray);
        if (travelPermission != null) JsonUtil.set("permission", travelPermission, json);
        JsonUtil.set("active", active, json);

        if (targetWorld != null) JsonUtil.set("world", targetWorld, json);
        if (toPosition != null) JsonUtil.set("from_position", fromPosition, json);
        if (toPosition != null) JsonUtil.set("to_position", toPosition, json);
        JsonUtil.set("relative", useRelativeTeleport, json);

        if (travelSound != null) JsonUtil.set("sound", travelSound, json);
        JsonUtil.set("keep_velocity", keepVelocity, json);

        return json;
    }

    @Override
    public void remove() {
        container.remove(this);
    }

    protected static CustomPortal load(CustomPortalContainer container, JsonObject json) {
        UUID uid = JsonUtil.getUUID("uid", json);
        if (uid == null) return null;
        String name = JsonUtil.getString("name", json);
        CustomPortal result = new CustomPortal(container, uid, name);
        result.regionId = JsonUtil.getString("region", json);

        if (json.has("game_modes")) {
            JsonArray gameModesArray = json.getAsJsonArray("game_modes");
            for (JsonElement element : gameModesArray) {
                if (!element.isJsonPrimitive()) continue;
                GameMode gameMode;
                try {
                    gameMode = GameMode.valueOf(element.getAsString());
                } catch (Exception e) {
                    continue;
                }

                result.allowedGameModes.add(gameMode);
            }
        }
        result.travelPermission = JsonUtil.getString("permission", json);
        result.active = JsonUtil.getBool("active", json);

        result.targetWorld = JsonUtil.getString("world", json);
        result.fromPosition = JsonUtil.getPosition("from_position", json);
        result.toPosition = JsonUtil.getPosition("to_position", json);
        result.useRelativeTeleport = JsonUtil.getBool("relative", json);

        result.keepVelocity = JsonUtil.getBool("keep_velocity", json);
        result.travelSound = JsonUtil.getString("sound", json);
        return result;
    }

    public static Optional<CustomPortal> get(ItemStack itemStack) {
        String portalIdString = ItemUtil.getString(itemStack, CustomPortal.ID_PROPERTY);
        if (portalIdString == null) return Optional.empty();
        UUID portalId;
        try {
            portalId = UUID.fromString(portalIdString);
        } catch (Exception e) {
            return Optional.empty();
        }

        return CustomPortalContainers.findPortal(portalId);
    }

    public static Optional<CustomPortal> get(World world, String regionId) {
        CustomPortalContainer container = CustomPortalContainers.get(world);
        return container.getPortal(regionId);
    }
}
