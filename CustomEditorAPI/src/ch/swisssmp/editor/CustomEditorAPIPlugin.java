package ch.swisssmp.editor;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomEditorAPIPlugin extends JavaPlugin {

    private static CustomEditorAPIPlugin plugin;

    protected static boolean debug = false;

    @Override
    public void onEnable() {
        plugin = this;

        Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        Bukkit.getLogger().info(getDescription().getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
    }

    public static CustomEditorAPIPlugin getInstance(){
        return plugin;
    }
}
