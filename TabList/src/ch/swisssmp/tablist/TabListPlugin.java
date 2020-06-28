package ch.swisssmp.tablist;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class TabListPlugin extends JavaPlugin {

    private static TabListPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        if(Bukkit.getPluginManager().getPlugin("PermissionManager")!=null){
            Bukkit.getPluginManager().registerEvents(new PermissionsListener(), this);
        }
        Bukkit.getPluginCommand("tablist").setExecutor(new PlayerCommand());

        Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        Bukkit.getLogger().info(getDescription().getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
    }

    public static TabListPlugin getInstance(){return plugin;}
}
