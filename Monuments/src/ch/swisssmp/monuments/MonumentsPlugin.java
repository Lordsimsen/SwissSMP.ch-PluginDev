package ch.swisssmp.monuments;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class MonumentsPlugin extends JavaPlugin {
    private static MonumentsPlugin plugin;
    private PluginDescriptionFile pdfFile;

    @Override
    public void onEnable() {
        plugin = this;
        pdfFile = getDescription();
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getPluginCommand("monuments").setExecutor(new PlayerCommand());
        MonumentEntries.reload();
        Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        PluginDescriptionFile pdfFile = getDescription();
        Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
}

    public static MonumentsPlugin getInstance(){
        return plugin;
    }

    public static String getPrefix() {
        return "["+plugin.getName()+"]";
    }
}
