package ch.swisssmp.adventuredungeons;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class AdventureDungeonsPlugin extends JavaPlugin {
    private static AdventureDungeonsPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        PlayerCommand playerCommand = new PlayerCommand();
        this.getCommand("join").setExecutor(playerCommand);
        this.getCommand("leave").setExecutor(playerCommand);
        this.getCommand("refuse").setExecutor(playerCommand);
        this.getCommand("invite").setExecutor(playerCommand);
        this.getCommand("ready").setExecutor(playerCommand);
        DungeonCommand dungeonCommand = new DungeonCommand();
        this.getCommand("dungeon").setExecutor(dungeonCommand);
        this.getCommand("dungeons").setExecutor(dungeonCommand);

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable() {
        for(DungeonInstance dungeonInstance : DungeonInstance.getAll()){
            dungeonInstance.delete(false);
        }
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        Bukkit.getLogger().info(getDescription().getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
    }

    public static AdventureDungeonsPlugin getInstance(){
        return plugin;
    }
}
