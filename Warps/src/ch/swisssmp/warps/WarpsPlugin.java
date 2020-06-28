package ch.swisssmp.warps;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class WarpsPlugin extends JavaPlugin {

    private static PluginDescriptionFile pdfFile;
    private static WarpsPlugin plugin;

    @Override
    public void onEnable(){
        plugin = this;
        pdfFile = getDescription();

        WarpPoints.loadWarps();

        plugin.getCommand("warp").setExecutor(new WarpCommand());

        Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
    }

    @Override
    public void onDisable(){
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
    }

    public JavaPlugin getInstance(){
        return plugin;
    }

    public static String getPrefix(){
        return "[" + ChatColor.LIGHT_PURPLE + "Warps" + ChatColor.RESET + "]";
    }
}
