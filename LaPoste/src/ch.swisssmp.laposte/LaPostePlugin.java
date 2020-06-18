package ch.swisssmp.laposte;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class LaPostePlugin extends JavaPlugin {

    private static LaPostePlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        Bukkit.getPluginCommand("laposte").setExecutor(new LaPosteCommand());
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        CraftingRecipes.registerCraftingRecipes();
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);
    }

    public static LaPostePlugin getInstance(){
        return plugin;
    }

    public static String getPrefix(){
        return "[" + ChatColor.YELLOW + "La Poste" + ChatColor.RESET + "]";
    }
}
