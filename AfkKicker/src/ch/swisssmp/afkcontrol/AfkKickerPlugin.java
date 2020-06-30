package ch.swisssmp.afkcontrol;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class AfkKickerPlugin extends JavaPlugin {
    private static AfkKickerPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        this.getCommand("afk").setExecutor(new AfkCommand());
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        (new AfkKicker(200, 5*20*60, 15*20*60)).initialize();

        Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        Bukkit.getLogger().info(getDescription().getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
    }

    public static AfkKickerPlugin getInstance(){
        return plugin;
    }

}
