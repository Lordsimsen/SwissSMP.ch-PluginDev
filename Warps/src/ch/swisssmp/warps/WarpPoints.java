package ch.swisssmp.warps;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.zvieriplausch.ZvieriArena;
import ch.swisssmp.zvieriplausch.ZvieriArenen;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
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
        unloadWarps();
        File dataFile = new File(WarpsPlugin.getInstance().getDataFolder(), "warps.yml");
        if(dataFile.exists()){
            WarpPoints.load(dataFile);
        } else{
            Bukkit.getLogger().info(WarpsPlugin.getPrefix() + " Couldn't load warps.yml");
        }
    }

    public static void load(File dataFile){
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(dataFile);
        for(String key : yamlConfiguration.getKeys(false)){
            WarpPoint warp;
            warpPoints.add(WarpPoint.load(warpSection));
        }
    }

    public static void saveWarps(){
        
    }

    public static void unloadWarps() {
        for(WarpPoint warp : warpPoints) {
            remove(warp);
        }
    }
}
