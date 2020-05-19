package ch.swisssmp.entitysafety;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class EntitySafetyPlugin extends JavaPlugin {

    private static EntitySafetyPlugin plugin;
    private EntityDeathLog log;

    @Override
    public void onEnable(){
        plugin = this;

        log = EntityDeathLog.load();

        Bukkit.getPluginCommand("safeentity").setExecutor(new SafeEntityCommand(this));
        Bukkit.getPluginCommand("packentity").setExecutor(new PackEntityCommand(this));
        Bukkit.getPluginCommand("unpackentity").setExecutor(new UnpackEntityCommand(this));
        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);

        Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        Bukkit.getLogger().info(getDescription().getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
    }

    public EntityDeathLog getLog(){
        return log;
    }

    public static String getPrefix(){
        return "["+plugin.getName()+"]";
    }

    public static EntitySafetyPlugin getInstance(){
        return plugin;
    }
}
