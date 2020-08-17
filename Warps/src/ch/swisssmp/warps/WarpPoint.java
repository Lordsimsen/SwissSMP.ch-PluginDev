package ch.swisssmp.warps;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.World;

public class WarpPoint {

    private final String name;
    private final Location warpLocation;
    private final World world;

    private WarpPoint(String name, Location location){
        this.name = name;
        this.warpLocation = location;
        this.world = location.getWorld();
    }

    public String getName(){
        return name;
    }

    public Location getWarpLocation(){
        return warpLocation;
    }

    public World getWorld(){
        return world;
    }

    public static WarpPoint create(String name, Location location){
        return new WarpPoint(name, location);
    }

    public static WarpPoint load(JsonObject warpSection, World world){
        String name = warpSection.get("name").getAsString();
        Location warpLocation = JsonUtil.getLocation("location", world, warpSection);
        if(name == null || warpLocation == null) return null;
        WarpPoint warp = new WarpPoint(name, warpLocation);
        return warp;
    }

    public JsonObject save(){
        JsonObject json = new JsonObject();
        JsonUtil.set("name", this.name, json);
        JsonUtil.set("location", this.warpLocation, json);
        return json;
    }
}