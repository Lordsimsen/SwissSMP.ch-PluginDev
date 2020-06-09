package ch.swisssmp.custompaintings;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomPaintingsPlugin extends JavaPlugin {

    private static CustomPaintingsPlugin plugin;

    @Override
    public void onEnable(){
        plugin = this;

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getPluginCommand("painting").setExecutor(new PaintingCommand());
        Bukkit.getPluginCommand("paintings").setExecutor(new PaintingsCommand());

        PaintingRenderer.reloadDitherer();

        PaintingDataContainer.loadAll();
        ChunkUtil.updateAll();

        Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        PaintingDataContainer.unloadAll();
        Bukkit.getLogger().info(getDescription().getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
    }

    public static String getPrefix(){
        return "["+plugin.getName()+"]";
    }

    public static CustomPaintingsPlugin getInstance(){
        return plugin;
    }
}
