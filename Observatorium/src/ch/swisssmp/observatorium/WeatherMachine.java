package ch.swisssmp.observatorium;

import ch.swisssmp.city.CitizenInfo;
import ch.swisssmp.utils.PlayerInfo;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WeatherMachine {

    private static final long delay = 10;
    static boolean activationAttempt = false;

    /*
    Checks whether the itemframe is part of a weather machine by iterating through its shape.
     */
    public static boolean isWeatherMachine(Location location){
        return false;
    }

    /*
    Sets the weather in [city] according to the placed Item (usages predefined and communicated in forum)
     */
    public static void applyCondition(ItemStack condition, Player player){
        boolean setTime = true;
        String label = "";
        WeatherType weatherType = WeatherType.CLEAR;
        switch(condition.getType()){
            case EMERALD:{
                label = "8000";
                break;
            }
            case IRON_INGOT:{
                label = "12000";
                break;
            }
            case GOLD_INGOT:{
                label = "18000";
                break;
            }
            case DIAMOND:{
                label = "24000";
                break;
            }
            case SUNFLOWER:{
                weatherType = WeatherType.CLEAR;
                setTime = false;
                break;
            }
            case CORNFLOWER:{
                weatherType = WeatherType.DOWNFALL;
                setTime = false;
                break;
            }
        }
//        ApplicableRegionSet citySet = CitizenInfo.get(stuff).getApplicableRegionSet();
        ApplicableRegionSet citySet = null;
        LocalPlayer localPlayer = ObservatoriumPlugin.worldGuardPlugin.wrapPlayer(player);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        Location location = BukkitAdapter.adapt(player.getLocation());
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(location);
        if(regionMatch(citySet, set)){
              for(ProtectedRegion region : citySet){
                  if(setTime) Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "region flag " + region.getId() + " time lock" + label);
                  else Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "region flag " + region.getId() + " weather lock" + weatherType);
              }
        }

    }

    private static boolean regionMatch(ApplicableRegionSet citySet, ApplicableRegionSet set){
        for(ProtectedRegion region : set){
            if(citySet.getRegions().contains(region)){
                continue;
            } else{
                return false;
            }
        }
        return true;
    }

    protected static void attemptActivation(){
        activationAttempt = true;
    }

    protected static void cancelActivation(){
        Bukkit.getScheduler().runTaskLater(ObservatoriumPlugin.getInstance(), () ->{
            activationAttempt = false;
        }, delay);
    }
}
