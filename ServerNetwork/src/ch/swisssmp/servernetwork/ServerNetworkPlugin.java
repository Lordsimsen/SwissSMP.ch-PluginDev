package ch.swisssmp.servernetwork;

import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.world.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerNetworkPlugin extends JavaPlugin {

    private static ServerNetworkPlugin plugin;
    private NetworkHandler networkHandler;

    @Override
    public void onEnable() {
        plugin = this;

        this.reload();

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable() {
        PluginDescriptionFile pdfFile = getDescription();
        HandlerList.unregisterAll(this);
        Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
    }

    public static String getPrefix(){
        return "["+ ChatColor.AQUA+plugin.getName()+ChatColor.RESET+"] ";
    }

    public static ServerNetworkPlugin getInstance(){
        return plugin;
    }

    protected void reload(){
        FileConfiguration file = getConfig();
        int port = file.getInt("port");
        if(networkHandler!=null) networkHandler.disconnect();
        networkHandler = new NetworkHandler(port);
        networkHandler.initialize();
    }
}
