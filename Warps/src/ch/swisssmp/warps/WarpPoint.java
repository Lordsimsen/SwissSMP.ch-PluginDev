package ch.swisssmp.warps;

import org.bukkit.Location;

public class WarpPoint {

    private final String name;
    private final Location warpLocation;

    private WarpPoint(String name, Location location){
        this.name = name;
        this.warpLocation = location;
        WarpPoints.addWarp(this);
    }

    public String getName(){
        return name;
    }

    public Location getWarpLocation(){
        return warpLocation;
    }

    public static WarpPoint setWarp(String name, Location location){
        return new WarpPoint(name, location);
    }
}
