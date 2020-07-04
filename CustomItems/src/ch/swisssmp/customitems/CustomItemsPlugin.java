package ch.swisssmp.customitems;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomItemsPlugin extends JavaPlugin {

    private static CustomItemsPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        PlayerCommand playerCommand = new PlayerCommand();
        this.getCommand("customitems").setExecutor(playerCommand);
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        CustomItems.reload();

        Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        PluginDescriptionFile pdfFile = getDescription();
        Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
    }

    public static CustomItemsPlugin getInstance(){
        return plugin;
    }
}
