package ch.swisssmp.loginrewards;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class LoginRewardsPlugin extends JavaPlugin {
    protected static LoginRewardsPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        Bukkit.getPluginCommand("loginrewards").setExecutor(new PlayerCommand());
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        Bukkit.getLogger().info(getDescription().getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
    }

    public static LoginRewardsPlugin getInstance() {
        return plugin;
    }

    public static String getPrefix() {
        return "[" + plugin.getName() + "]";
    }
}
