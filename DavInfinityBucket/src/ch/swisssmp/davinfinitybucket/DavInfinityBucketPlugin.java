package ch.swisssmp.davinfinitybucket;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class DavInfinityBucketPlugin extends JavaPlugin {
    private static DavInfinityBucketPlugin instance;

    @Override
    public void onEnable() {

        instance = this;

        InfinityBucket.createRecipe();

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        Bukkit.getLogger().info(getDescription().getName() + " Has been enabled (Version: " +
                getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable() {

        HandlerList.unregisterAll(this);

        Bukkit.getLogger().info(getDescription().getName() + " Has been disabled (Version: " +
                getDescription().getVersion() + ")");
    }

    public static DavInfinityBucketPlugin getInstance() {
        return instance;
    }
}
