package ch.swisssmp.events;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import org.bukkit.World;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class EventArenas {

    private static List<EventArena> arenasList;

    public static List<EventArena> getArenasList(){
        return arenasList;
    }

    public static List<EventArena> getArenas(World world){
        List<EventArena> result = new ArrayList<EventArena>();
        for(EventArena arena : arenasList) {
            if(arena.getWorld() == world) {
                result.add(arena);
            }
        }
        return result;
    }


    public static void save(World world, String pluginDirectoryName) {
        File pluginDirectory = new File(world.getWorldFolder(), "plugindata/" + pluginDirectoryName);
        File dataFile = new File(pluginDirectory, "arenen.yml");

        if(!pluginDirectory.exists()) {
            pluginDirectory.mkdirs();
        }

        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        ConfigurationSection arenenSection = yamlConfiguration.createSection("arenen");

        for(EventArena arena : EventArenas.getArenas(world)) {
            ConfigurationSection arenaSection = arenenSection.createSection(arena.getArena_id().toString());
            arena.save(arenaSection);
        }
        yamlConfiguration.save(dataFile);
    }

    public static void load(World world, String pluginDirectoryName) {
        unload(world);
        File dataFile = new File(world.getWorldFolder(), "plugindata/" + pluginDirectoryName + "/arenen.yml");
        if(dataFile.exists()) {
            EventArenas.load(world, dataFile);
        }
    }

    public static void load(World world, File dataFile) {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(dataFile);
        if(yamlConfiguration.contains("arenen")) {
            ConfigurationSection arenenSection = yamlConfiguration.getConfigurationSection("arenen");
            for(String key : arenenSection.getKeys(false)) {
                ConfigurationSection arenaSection = arenenSection.getConfigurationSection(key);
                EventArena arena = EventArena.load(world, arenaSection);
                arenasList.add(arena);
            }
        }
    }

    public static void unload(World world) {
        for(EventArena arena : EventArenas.getArenas(world)) {
            remove(arena);
        }
    }

    protected static void remove(EventArena arena){
        arenasList.remove(arena);
        save(arena.getWorld(), EventPlugin.getDefaultDirectoryName());
    }
}
