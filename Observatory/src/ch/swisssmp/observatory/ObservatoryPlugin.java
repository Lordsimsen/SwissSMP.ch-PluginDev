package ch.swisssmp.observatory;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class ObservatoryPlugin extends JavaPlugin {

    private static JavaPlugin observatoryPlugin;
    protected static WorldGuardPlugin worldGuardPlugin;

    @Override
    public void onEnable(){
        worldGuardPlugin = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        if(worldGuardPlugin == null) Bukkit.getLogger().info(getPrefix() + " Couldn't find WorldGuard");
        observatoryPlugin = this;

        ObservatoryEntries.reload();

        Bukkit.getLogger().info(this.getName() + " has been enabled (Version " + this.getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable(){
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);

        Bukkit.getLogger().info(this.getName() + " has been disabled (Version " + this.getDescription().getVersion() + ")");
    }

    public static JavaPlugin getInstance(){
        return observatoryPlugin;
    }

    public static String getPrefix(){
        return ChatColor.AQUA + "[" + ChatColor.LIGHT_PURPLE + "Observatorium" + ChatColor.AQUA + "]";
    }


}
