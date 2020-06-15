package ch.swisssmp.camerastudio;

import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.utils.Position;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

public class ViewerInfo {
    private final UUID playerUid;

    private final String world;
    private final Position position;

    private final GameMode gameMode;
    private final boolean flying;
    private final boolean gravity;

    protected ViewerInfo(UUID uid, String world, Position position, GameMode gameMode, boolean flying, boolean gravity){
        this.playerUid = uid;

        this.world = world;
        this.position = position;

        this.gameMode = gameMode;
        this.flying = flying;
        this.gravity = gravity;
    }

    public UUID getUniqueId(){
        return playerUid;
    }

    public GameMode getGameMode(){
        return gameMode;
    }

    public boolean isFlying(){
        return flying;
    }

    public boolean hasGravity(){
        return gravity;
    }

    public void apply(Player player){
        apply(player,true);
    }

    public void apply(Player player, boolean teleport){
        if(teleport){
            World world = Bukkit.getWorld(this.world);
            if(world!=null){
                Location location = position.getLocation(world);
                player.teleport(location);
            }
            else{
                Bukkit.getLogger().warning(CameraStudioPlugin.getPrefix()+" Kann "+player.getName()+" nicht in die Welt "+world+" teleportieren, da diese nicht gefunden wurde.");
            }
        }

        player.setGameMode(gameMode);
        player.setFlying(flying);
        player.setGravity(gravity);
        if(player.hasPotionEffect(PotionEffectType.SLOW)){
            player.removePotionEffect(PotionEffectType.SLOW);
        }
    }

    public boolean save(){
        JsonObject json = new JsonObject();
        JsonUtil.set("uuid", playerUid, json);
        JsonUtil.set("world", world, json);
        JsonUtil.set("position", position, json);
        JsonUtil.set("gamemode", gameMode.toString(), json);
        JsonUtil.set("flying", flying, json);
        JsonUtil.set("gravity", gravity, json);
        File file = getViewerFile(playerUid);
        return JsonUtil.save(file, json);
    }

    public boolean delete(){
        File file = getViewerFile(playerUid);
        if(!file.exists()) return true;
        return file.delete();
    }

    protected static ViewerInfo of(Player player){
        return new ViewerInfo(player.getUniqueId(), player.getWorld().getName(), new Position(player.getLocation()), player.getGameMode(), player.isFlying(), player.hasGravity());
    }

    protected static Optional<ViewerInfo> load(Player player){
        return load(getViewerFile(player));
    }

    protected static Optional<ViewerInfo> load(File file){
        if(!file.exists()){
            return Optional.empty();
        }
        JsonObject json = JsonUtil.parse(file);
        if(json==null){
            Bukkit.getLogger().warning(CameraStudioPlugin.getPrefix()+" Invalid Json: "+file.getAbsoluteFile());
            return Optional.empty();
        }
        ViewerInfo viewerInfo = load(json);
        return viewerInfo!=null ? Optional.of(viewerInfo) : Optional.empty();
    }

    protected static ViewerInfo load(JsonObject json){
        UUID playerUid;
        try{
            String playerUidString = JsonUtil.getString("uuid", json);
            if(playerUidString==null){
                Bukkit.getLogger().warning(CameraStudioPlugin.getPrefix()+" Invalid UUID: "+json);
                return null;
            }
            playerUid = UUID.fromString(playerUidString);
        }
        catch(Exception e){
            e.printStackTrace();
            Bukkit.getLogger().warning(CameraStudioPlugin.getPrefix()+" Invalid Json: "+json);
            return null;
        }

        String world = JsonUtil.getString("world", json);
        Position position = JsonUtil.getPosition("position", json);
        GameMode gameMode = GameMode.valueOf(JsonUtil.getString("gamemode", json));
        boolean flying = JsonUtil.getBool("flying", json);
        boolean gravity = JsonUtil.getBool("gravity", json);
        return new ViewerInfo(playerUid, world, position, gameMode, flying, gravity);
    }

    public static File getViewerFile(Player player){
        return getViewerFile(player.getUniqueId());
    }

    public static File getViewerFile(UUID playerUid){
        return new File(CameraStudioPlugin.getInstance().getDataFolder(), "viewer_data/"+playerUid+".json");
    }
}
