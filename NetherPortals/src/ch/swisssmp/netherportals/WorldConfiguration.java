package ch.swisssmp.netherportals;

import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.world.WorldManager;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BlockVector;

import java.io.File;

public class WorldConfiguration {

    private final World world;

    private String targetWorld;
    private int targetCoordinateFactor;
    private CoordinateOperation operation;
    private boolean allowPortalCreation = true;
    private boolean enabled = true;

    private WorldConfiguration(World world){
        this.world = world;
    }

    public World getBukkitWorld(){
        return world;
    }

    public String getTargetWorld(){
        return targetWorld;
    }

    public void setTargetWorld(String world){
        targetWorld = world;
    }

    public int getTargetCoordinateFactor(){
        return targetCoordinateFactor;
    }

    public void setTargetCoordinateFactor(int factor){
        this.targetCoordinateFactor = factor;
    }

    public CoordinateOperation getOperation(){
        return operation;
    }

    public void setOperation(CoordinateOperation operation){
        this.operation = operation;
    }

    public boolean getAllowPortalCreation(){
        return allowPortalCreation;
    }

    public void setAllowPortalCreation(boolean allow){
        allowPortalCreation = allow;
    }

    public boolean isEnabled(){
        return enabled;
    }

    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }

    public boolean isSetupComplete(){
        return targetWorld!=null && targetCoordinateFactor>0 && operation!=null;
    }

    public void unload(){
        // add code if needed
    }

    public Location createToLocation(Location from){
        World toWorld = Bukkit.getWorld(targetWorld);
        if(toWorld==null){
            return null;
        }

        Location cached = PortalLinkCache.getCached(from);
        if(cached!=null) {
            // Bukkit.getLogger().info("[NetherPortalFixer] Zwischengespeicherten Link gefunden: "+cached.getWorld().getName()+", "+cached.getX()+", "+cached.getY()+", "+cached.getZ());
            return cached;
        }

        double x = operation.apply(from.getX(), targetCoordinateFactor);
        double y = from.getY();
        double z = operation.apply(from.getZ(), targetCoordinateFactor);
        Location remappedLocation = new Location(toWorld, x, y, z, from.getYaw(), from.getPitch());

        boolean toWorldIsNether = toWorld.getEnvironment()== World.Environment.NETHER;
        Location targetLocation = NetherPortalAgent.getTargetLocation(remappedLocation, toWorldIsNether ? 64 : 128, 16, new BlockVector(4,4,3), new BlockVector(4,4,1), allowPortalCreation);
        targetLocation.setYaw(from.getYaw());
        targetLocation.setPitch(from.getPitch());

        PortalLinkCache.create(from, targetLocation, 60*20); // 60s * 20tps
        return targetLocation;
    }

    private void load(JsonObject json){
        this.targetWorld = JsonUtil.getString("target", json);
        this.targetCoordinateFactor = json.has("factor") ? JsonUtil.getInt("factor", json) : 1;
        this.operation = json.has("operation") ? CoordinateOperation.parse(JsonUtil.getString("operation", json)) : CoordinateOperation.MULTIPLY;
        this.allowPortalCreation = !json.has("portal_creation") || JsonUtil.getBool("portal_creation", json);
        this.enabled = !json.has("enabled") || JsonUtil.getBool("enabled", json);
    }

    public void save(){
        JsonObject json = new JsonObject();
        if(targetWorld!=null) JsonUtil.set("target", targetWorld, json);
        if(targetCoordinateFactor>0) JsonUtil.set("factor", targetCoordinateFactor, json);
        if(operation!=null) JsonUtil.set("operation", operation.toString(), json);
        JsonUtil.set("portal_creation", allowPortalCreation, json);
        JsonUtil.set("enabled", enabled, json);
        File file = getFile(world);
        JsonUtil.save(file, json);
    }

    public void applyDefaults(){
        this.allowPortalCreation = true;
        this.enabled = true;
        switch(world.getEnvironment()){
            case NORMAL:{
                if(Bukkit.getWorlds().size()>1 && world==Bukkit.getWorlds().get(0)){
                    this.targetWorld = Bukkit.getWorlds().get(1).getName();
                }
                this.targetCoordinateFactor = 8;
                this.operation = CoordinateOperation.DIVIDE;
                break;
            }
            case NETHER:{
                if(Bukkit.getWorlds().size()>1 && world==Bukkit.getWorlds().get(1)){
                    this.targetWorld = Bukkit.getWorlds().get(0).getName();
                }
                this.targetCoordinateFactor = 8;
                this.operation = CoordinateOperation.MULTIPLY;
                break;
            }
            case THE_END:
            default:{
                this.targetCoordinateFactor = 1;
                this.operation = CoordinateOperation.MULTIPLY;
                break;
            }
        }
    }

    public static WorldConfiguration get(World world){
        return WorldConfigurations.get(world);
    }

    protected static WorldConfiguration load(World world){
        WorldConfiguration result = new WorldConfiguration(world);
        File file = getFile(world);
        JsonObject json = file!=null && file.exists() ? JsonUtil.parse(file) : null;
        if(json==null){
            result.applyDefaults();
            return result;
        }
        result.load(json);
        return result;
    }

    private static File getFile(World world){
        return new File(WorldManager.getPluginDirectory(NetherPortalsPlugin.getInstance(), world), "config.yml");
    }
}
