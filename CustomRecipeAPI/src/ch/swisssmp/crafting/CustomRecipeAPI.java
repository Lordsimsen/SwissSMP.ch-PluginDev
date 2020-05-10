package ch.swisssmp.crafting;

import ch.swisssmp.crafting.brewing.BrewingListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomRecipeAPI extends JavaPlugin {
    private static CustomRecipeAPI plugin;

    @Override
    public void onEnable() {
        plugin = this;

        Bukkit.getPluginManager().registerEvents(new BrewingListener(), this);

        Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        PluginDescriptionFile pdfFile = getDescription();
        Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
    }

    public static void addRecipe(CustomRecipe recipe){
        CustomRecipes.add(recipe);
    }

    public static void removeRecipe(NamespacedKey key){
        CustomRecipes.remove(key);
    }

    public static void removeRecipes(Plugin plugin){
        CustomRecipes.remove(plugin);
    }

    public static CustomRecipeAPI getInstance(){
        return plugin;
    }

    public static String getPrefix(){
        return "["+ ChatColor.GRAY+plugin.getName()+ChatColor.RESET+"] ";
    }
}
