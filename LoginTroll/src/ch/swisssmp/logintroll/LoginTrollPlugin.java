package ch.swisssmp.logintroll;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class LoginTrollPlugin extends JavaPlugin {

    protected static LoginTrollPlugin plugin;

    @Override
    public void onEnable(){
        plugin = this;

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getPluginCommand("logintroll").setExecutor(new LoginTrollCommand());

        NicknameMap.load();

        Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable() {

        NicknameMap.clear();

        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        PluginDescriptionFile pdfFile = getDescription();
        Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
    }

    public static String getPrefix(){
        return "["+plugin.getName()+"]";
    }

    public static LoginTrollPlugin getInstance(){
        return plugin;
    }
}
