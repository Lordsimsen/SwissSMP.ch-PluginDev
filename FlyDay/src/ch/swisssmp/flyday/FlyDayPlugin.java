package ch.swisssmp.flyday;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class FlyDayPlugin extends JavaPlugin {

    public static FlyDayPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        Bukkit.getLogger().info(getDescription() + " has been enabled (Version: " + getDescription().getVersion() + ")");

        FlyDayCommand flyDayCommand = new FlyDayCommand();
        this.getCommand("FlyDay").setExecutor(flyDayCommand);

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        if(Bukkit.getPluginManager().isPluginEnabled("Lift")){
            Bukkit.getPluginManager().registerEvents(new LiftHandler(), this);
        }

        FlyDay.updateState();
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        PluginDescriptionFile pdfFile = getDescription();
        Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
    }

    public static FlyDayPlugin getInstance(){
        return plugin;
    }

    public static String getPrefix(){
        return "["+ ChatColor.YELLOW+plugin.getName()+ChatColor.RESET+"]";
    }
}
