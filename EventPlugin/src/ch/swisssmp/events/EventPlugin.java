package ch.swisssmp.events;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class EventPlugin extends JavaPlugin {

    @Override
    public void onEnable(){
        for (World world : Bukkit.getWorlds()) {
            EventArenas.load(world, this.getName());
        }
    }

    @Override
    public void onDisable(){
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    public abstract Plugin getInstance();
    public abstract String getPrefix();
    public abstract String getDirectoryName();

    public static String getDefaultDirectoryName() {
        return "";
    }

    public static String getDefaultPrefix(){
        return "[" + ChatColor.DARK_PURPLE + "EventPlugin" + ChatColor.RESET + "]";
    }
}
