package ch.swisssmp.world;

import ch.swisssmp.world.border.WorldBorderCommand;
import ch.swisssmp.world.border.WorldBorderManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class WorldManagerPlugin extends JavaPlugin {

    private static WorldManagerPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        Bukkit.getPluginCommand("world").setExecutor(new WorldCommand());
        Bukkit.getPluginCommand("worlds").setExecutor(new WorldsCommand());
        Bukkit.getPluginCommand("worldborder").setExecutor(new WorldBorderCommand());

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        if(Bukkit.getPluginManager().getPlugin("WorldGuard")!=null) Bukkit.getPluginManager().registerEvents(new WorldGuardPluginHandler(), this);

        WorldManager.loadWorlds();
        WorldBorderManager.startBorderChecker();

        if(Bukkit.getPluginManager().getPlugin("ResourcepackManager")!=null){
            Bukkit.getPluginManager().registerEvents(new ResourcepackListener(), this);
        }

        Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);
        PluginDescriptionFile pdfFile = getDescription();
        Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
    }

    public static String getPrefix() {
        return ChatColor.RESET+"["+ChatColor.GRAY+plugin.getName()+ChatColor.RESET+"]";
    }

    public static WorldManagerPlugin getInstance(){
        return plugin;
    }
}
