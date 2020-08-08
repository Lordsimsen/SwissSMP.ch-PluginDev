package ch.swisssmp.weaver;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class WeaverPlugin extends JavaPlugin {

    private static WeaverPlugin plugin;

    @Override
    public void onEnable(){
        plugin = this;

        Bukkit.getPluginManager().registerEvents(new EventListener(), plugin);
        Bukkit.getPluginCommand("reloadbanners").setExecutor(new ReloadBannersCommand());

        CityBanners.reloadBanners();

        Bukkit.getLogger().info(getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable(){
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        CityBanners.unloadBanners();
        Bukkit.getLogger().info(getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
    }

    public static JavaPlugin getInstance(){
        return plugin;
    }

    public static String getPrefix(){
        return ChatColor.BLUE + "[" + ChatColor.BOLD + "Weberei" + ChatColor.BLUE + "]";
    }
}
