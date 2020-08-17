package ch.swisssmp.warps;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class WarpsPlugin extends JavaPlugin {

    private static WarpsPlugin plugin;

    @Override
    public void onEnable(){
        plugin = this;

        for(World world : Bukkit.getWorlds()) {
            WarpPoints.loadWarps(world);
        }

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        Bukkit.getPluginCommand("warp").setExecutor(new WarpCommand());
        Bukkit.getPluginCommand("warps").setExecutor(new WarpsCommand());
        Bukkit.getPluginCommand("setwarp").setExecutor(new SetWarpCommand());
        Bukkit.getPluginCommand("removewarp").setExecutor(new RemoveWarpCommand());

        Bukkit.getLogger().info(getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable(){
        WarpPoints.unloadWarps();
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        Bukkit.getLogger().info(getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
    }

    public static JavaPlugin getInstance(){
        return plugin;
    }

    public static String getPrefix(){
        return "[" + ChatColor.LIGHT_PURPLE + "Warps" + ChatColor.RESET + "]";
    }
}
