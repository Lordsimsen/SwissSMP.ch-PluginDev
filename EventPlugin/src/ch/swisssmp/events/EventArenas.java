package ch.swisssmp.events;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.YamlConfiguration;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventArenas {

    private static List<EventArena> arenasList;
    private EventPlugin plugin;

    public List<EventArena> getArenasList(){
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

    public EventArenas(EventPlugin plugin){
        this.plugin = plugin;
        arenasList = new ArrayList<>();
    }

    public EventArena get(String name, boolean exactMatch){
        for(EventArena arena : arenasList){
            if(exactMatch && !arena.getName().toLowerCase().equals(name.toLowerCase())) {
                continue;
            }
            if(arena.getName().toLowerCase().contains(name.toLowerCase())) {
                return arena;
            }
        }
        return null;
    }

    public EventArena get(UUID arena_id) {
        for(EventArena arena : arenasList){
            if(arena.getArena_id().equals(arena_id)) return arena;
        }
        return null;
    }

    public EventArena get(ItemStack tokenStack){
        String uuidString = ItemUtil.getString(tokenStack, "arena");
        if(uuidString == null) {
            return null;
        }
        UUID arena_id = UUID.fromString(uuidString);
        if(arena_id == null) {
            return null;
        }
        return get(arena_id);
    }



    public void save(World world) {
        File pluginDirectory = plugin.getDataFolder();
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

    public void load(World world) {
        unload(world);
        File dataFile = new File(plugin.getDataFolder(), "arenen.yml");
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

    public void unload(World world) {
        for(EventArena arena : EventArenas.getArenas(world)) {
            remove(arena);
        }
    }

    protected void remove(EventArena arena){
        arenasList.remove(arena);
        save(arena.getWorld());
    }
}
