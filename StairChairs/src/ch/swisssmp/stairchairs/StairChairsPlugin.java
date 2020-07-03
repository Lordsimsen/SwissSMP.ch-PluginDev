package ch.swisssmp.stairchairs;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class StairChairsPlugin extends JavaPlugin {
    protected static StairChairsPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable() {
        StairChairs.unload();
        HandlerList.unregisterAll(this);
        Bukkit.getLogger().info(getDescription().getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
    }

    public static StairChairsPlugin getInstance(){
        return plugin;
    }

    public static String getPrefix(){
        return "["+plugin.getName()+"]";
    }
}
