package ch.swisssmp.events;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class EventPlugin extends JavaPlugin {

    private EventArenas arenas;
    private EventPlugin plugin = this;

    @Override
    public void onEnable(){
        arenas = new EventArenas(this);
        for (World world : Bukkit.getWorlds()) {
            arenas.load(world);
        }
    }

    @Override
    public void onDisable(){
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    public Plugin getInstance(){
        return plugin;
    }

    public abstract String getPrefix();
}
