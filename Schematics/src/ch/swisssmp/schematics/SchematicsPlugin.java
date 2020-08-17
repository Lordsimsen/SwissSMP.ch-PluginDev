package ch.swisssmp.schematics;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class SchematicsPlugin extends JavaPlugin {

    private static SchematicsPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        // Bukkit.getPluginCommand("schematic").setExecutor(new SchematicCommand());
        // Bukkit.getPluginCommand("schematics").setExecutor(new SchematicsCommand());

        Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info(getDescription().getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
    }

    public static SchematicsPlugin getInstance(){
        return plugin;
    }

    public static String getPrefix() {
        return "["+plugin.getName()+"]";
    }
}
