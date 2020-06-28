package ch.swisssmp.warps;

import java.util.List;

public class WarpPoints {

    private static List<WarpPoint> warpPoints;

    public static void addWarp(WarpPoint warp){
        warpPoints.add(warp);
    }

    public static WarpPoint getWarp(String name){
        for(WarpPoint warp : warpPoints){
            if(!warp.getName().equalsIgnoreCase(name)) continue;
            return warp;
        }
        return null;
    }

    public static void loadWarps(){
        //Todo load warps from file
    }

    public static void saveWarps(){
        
    }
}
