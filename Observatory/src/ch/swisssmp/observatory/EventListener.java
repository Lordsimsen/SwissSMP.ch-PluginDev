package ch.swisssmp.observatory;

import ch.swisssmp.city.City;
import ch.swisssmp.utils.SwissSMPler;
import com.sk89q.worldedit.world.weather.WeatherType;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {

    private static boolean triggered = false;
    private static Player triggerPlayer = null;

    protected static void setTriggered(boolean trigger, Player triggerer){
        triggered = trigger;
        triggerPlayer = triggerer;
    }

    @EventHandler
    private void onParametersSet(PlayerInteractEntityEvent event){
        Entity entity = event.getRightClicked();
        if(!(entity instanceof ItemFrame)) return;
        Player player = event.getPlayer();
        if(!player.hasPermission("observatory.use")) return;
        ItemFrame frame = (ItemFrame) event.getRightClicked();
        ItemStack itemStack = frame.getItem();
        //check whether material is one of the predefined ones. if not, return

        String timeLock = ""; //TODO read from item/material
        String weather = "";
        org.bukkit.WeatherType weatherT = org.bukkit.WeatherType.valueOf(weather);
        WeatherType weatherLock = new WeatherType(weatherT.toString()); //TODO read from item/material

        Block block = frame.getLocation().getBlock();
        Observatory observatory = ObservatoryEntries.blockinObservatoryArea(block);
        if(observatory == null) return;

        if(!MultiblockStructure.isMultiblockStructure(block)) return;

        if(!triggered) {
            setTriggered(true, player);
            Bukkit.getScheduler().runTaskLater(ObservatoryPlugin.getInstance(), ()->{
                setTriggered(false, null);
            },10L);
            return;
        }
        City city = observatory.getCity();
        if(city == null) {
            Bukkit.getLogger().info(ObservatoryPlugin.getPrefix() + " Couldn't find city to observatory: " + observatory.getId());
            return;
        }

        SwissSMPler.get(player).sendActionBar(ChatColor.GREEN + "Parameter angenommen! Schalte Wettermaschine an..");
        SwissSMPler.get(triggerPlayer).sendActionBar(ChatColor.GREEN + "Parameter angenommen! Schalte Wettermaschine an..");

        //TODO could play some sounds, maybe power some lamps to make it seem the MultiblockStructure is processing.
        Bukkit.getScheduler().runTaskLater(ObservatoryPlugin.getInstance(), () -> {
            ApplicableRegionSet cityRegions = city.getApplicableRegionSet(); //TODO by the dude who makes the zones plugin I guess
            for(ProtectedRegion cityRegion : cityRegions){
                cityRegion.setFlag(Flags.WEATHER_LOCK, weatherLock);
                cityRegion.setFlag(Flags.TIME_LOCK, timeLock);
        //            String id = cityRegion.getId();
        //            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "rg flag " + id + " time-lock" + timeLock);
        //            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "rg flag " + id + " weather-lock" + weatherLock.toString());
            }
            player.sendMessage(ObservatoryPlugin.getPrefix() + " Zeit und Wetter in " + ChatColor.GOLD + city.getName() + ChatColor.RESET + " auf "
                    + ChatColor.AQUA + timeLock + ChatColor.RESET + " und " + ChatColor.AQUA + weatherLock.toString() + ChatColor.RESET + " gesetzt");

            triggerPlayer.sendMessage(ObservatoryPlugin.getPrefix() + " Zeit und Wetter in " + ChatColor.GOLD + city.getName() + ChatColor.RESET + " auf "
                    + ChatColor.AQUA + timeLock + ChatColor.RESET + " und " + ChatColor.AQUA + weatherLock.toString() + ChatColor.RESET + " gesetzt");
        }, 50L);
    }

    @EventHandler
    private void onParameterRemove(EntityDamageByEntityEvent event){
        Entity entity = event.getEntity();
        if(!(entity instanceof ItemFrame)) return;
        Block block = entity.getLocation().getBlock();
        if(!MultiblockStructure.isMultiblockStructure(block)) return;
        Observatory observatory = ObservatoryEntries.blockinObservatoryArea(block);
        if(observatory == null) return;

        City city = observatory.getCity();
        ApplicableRegionSet cityRegions = city.getApplicableRegionSet(); //TODO by the dude who makes the zones plugin I guess
        for(ProtectedRegion cityRegion : cityRegions){
            cityRegion.setFlag(Flags.WEATHER_LOCK, null);
            cityRegion.setFlag(Flags.TIME_LOCK, null);
//            String id = cityRegion.getId();
//            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "rg flag " + id + " time-lock");
//            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "rg flag " + id + " weather-lock");
        }
    }
}
