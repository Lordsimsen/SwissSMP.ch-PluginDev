package ch.swisssmp.observatorium;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ObservatoriumPlugin extends JavaPlugin {

    private static Plugin observatoriumPlugin;
    protected static WorldGuardPlugin worldGuardPlugin;

    @Override
    public void onEnable(){
        worldGuardPlugin = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        if(worldGuardPlugin == null) Bukkit.getLogger().info(getPrefix() + " Couldn't find WorldGuard");
        observatoriumPlugin = this;
    }

    @Override
    public void onDisable(){
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    public static Plugin getInstance(){
        return observatoriumPlugin;
    }

    public static String getPrefix(){
        return "[" + ChatColor.LIGHT_PURPLE + "Observatorium" + ChatColor.RESET + "]";
    }


}
