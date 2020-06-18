package ch.swisssmp.ceremonies;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CeremoniesPlugin extends JavaPlugin {

    @Override
    public void onEnable(){
        Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
        Bukkit.getPluginCommand("zuschauen").setExecutor(new SpectateCommand());
    }

    @Override
    public void onDisable(){
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        Bukkit.getLogger().info(getDescription().getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
    }

    public String getPrefix(){
        return "[" + ChatColor.GOLD + "Ceremonies" + ChatColor.RESET + "]";
    }
}
