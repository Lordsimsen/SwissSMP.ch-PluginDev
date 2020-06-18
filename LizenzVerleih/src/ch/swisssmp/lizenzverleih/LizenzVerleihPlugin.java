package ch.swisssmp.lizenzverleih;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class LizenzVerleihPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        plugin = this;

        LivemapInterface.link();
        this.getCommand("addon").setExecutor(new AddonCommand());

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        Techtrees.loadAll();

        Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
    }

}
