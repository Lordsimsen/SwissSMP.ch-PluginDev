package ch.swisssmp.chatmanager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatManagerPlugin extends JavaPlugin {
    private static ChatManagerPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        this.getCommand("chat").setExecutor(new ChatCommand());
        this.getCommand("tell").setExecutor(new TellCommand());
        this.getCommand("reply").setExecutor(new ReplyCommand());

        Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info(getDescription().getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
    }

    public static ChatManagerPlugin getInstance(){
        return plugin;
    }
}
