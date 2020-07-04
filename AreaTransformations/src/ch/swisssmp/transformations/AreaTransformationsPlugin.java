package ch.swisssmp.transformations;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class AreaTransformationsPlugin extends JavaPlugin {

    private static AreaTransformationsPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        this.getCommand("transformation").setExecutor(new TransformationCommand());
        this.getCommand("transformations").setExecutor(new TransformationsCommand());
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        TransformationContainers.loadAll();

        Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        TransformationContainers.unloadAll();
        Bukkit.getLogger().info(getDescription().getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
    }

    public static AreaTransformationsPlugin getInstance(){
        return plugin;
    }
}
