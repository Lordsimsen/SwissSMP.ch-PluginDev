package ch.swisssmp.world;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.FileUtil;
import ch.swisssmp.world.border.WorldBorder;
import ch.swisssmp.world.border.WorldBorderManager;

public class WorldLoader {

    private static final boolean DEBUG = true;

    private static void print(String message) {
        if (!DEBUG) return;
        Bukkit.getLogger().info(WorldManagerPlugin.getPrefix() + " " + message);
    }

    protected static World load(String worldName, ConfigurationSection dataSection) {
        //Clear old World Border
        WorldBorderManager.removeWorldBorder(worldName);
        //Load Bukkit World
        World result = Bukkit.getWorld(worldName);
        print("Lade Welt " + worldName + "...");

        if (result == null) {
            print("Welt " + worldName + " nicht gefunden. Versuche, diese zu erstellen...");
            result = WorldLoader.createWorld(worldName, dataSection);
        }

        if (result == null) {
            print("Welt " + worldName + " konnte weder geladen noch erstellt werden!");
            return result;
        }

        if (dataSection.contains("world_border")) {
            print("Lade Weltrand für Welt " + worldName + "!");
            //Load World Border
            WorldBorder worldBorder = WorldBorder.create(dataSection.getConfigurationSection("world_border"));
            //Apply World Border
            WorldBorderManager.setWorldBorder(worldName, worldBorder);
            print("Weltrand für Welt " + worldName + " geladen!");
        }

        print("Welt " + worldName + " geladen!");

        return result;
    }

    private static World createWorld(String worldName, ConfigurationSection dataSection) {
        print("Erstelle Welt " + worldName + "...");
        //Copy Advancements
        WorldLoader.copyDefaultAdvancements(worldName);
        //Make World Creator
        WorldCreator creator = WorldLoader.getWorldCreator(worldName, dataSection);
        print("WorldCreator erstellt mit folgenden Einstellungen:\n" +
                "Name: " + creator.name() + "\n" +
                "Umgebung: "+creator.environment()+"\n"+
                "Oberfläche: "+creator.type()+"\n"+
                "Seed: "+creator.seed()
        );
        //Create World
        World result = Bukkit.createWorld(creator);
        if (result == null){
            print("Welt " + worldName + " konnte nicht erstellt werden!");
            return null;
        }
        //Set time
        long time = dataSection.contains("time") ? dataSection.getLong("time") : 0;
        result.setTime(time);
        print("Setze Zeit auf "+time+".");
        //Apply Game Rules
        if (dataSection.contains("gamerules")) {
            print("Welte Spielregeln an...");
            WorldLoader.applyGameRules(result, dataSection.getConfigurationSection("gamerules"));
        }
        //Set Spawn
        print("Setze Weltspawn...");
        result.setSpawnLocation(dataSection.getInt("spawn_x"), dataSection.getInt("spawn_y"), dataSection.getInt("spawn_z"));

        print("Welt "+worldName+" erstellt!");
        return result;
    }

    private static WorldCreator getWorldCreator(String worldName, ConfigurationSection dataSection) {
        WorldCreator result = new WorldCreator(worldName);
        result.environment(Environment.valueOf(dataSection.getString("environment")));
        result.generateStructures(dataSection.getBoolean("generate_structures"));
        result.seed(dataSection.getLong("seed"));
        WorldType worldType = getWorldType(dataSection.getString("world_type"));
        result.type(worldType);
        return result;
    }

    private static WorldType getWorldType(String s){
        if(s==null) return WorldType.NORMAL;
        try{
            return WorldType.valueOf(s);
        }
        catch(Exception ignored){
            switch(s.toUpperCase()){
                case "BUFFET": return WorldType.FLAT;
                case "LARGE_BIOMES": return WorldType.LARGE_BIOMES;
                case "AMPLIFIED": return WorldType.AMPLIFIED;
                case "NORMAL":
                default: return WorldType.NORMAL;
            }
        }
    }

    /**
     * Copies all advancements from the main World to the target World. This is necessary to maintain the advancement system.
     *
     * @param worldName - The target World to copy the advancements to
     */
    private static void copyDefaultAdvancements(String worldName) {
        File mainWorldAdvancementsFile = new File(Bukkit.getWorldContainer(), Bukkit.getWorlds().get(0).getName() + "/data/advancements");
        if (!mainWorldAdvancementsFile.exists()) return;
        File worldAdvancementsFile = new File(Bukkit.getWorldContainer(), worldName + "/data/advancements");
        if (worldAdvancementsFile.exists()) {
            FileUtil.deleteRecursive(worldAdvancementsFile);
        }
        FileUtil.copyDirectory(mainWorldAdvancementsFile, worldAdvancementsFile);
    }

    /**
     * Applies all gamerules from the gamerulesSection to the given World
     *
     * @param world            - The world to apply the gamerules to
     * @param gamerulesSection - A ConfigurationSection with gamerules and associated values
     */
    @SuppressWarnings("deprecation")
    private static void applyGameRules(World world, ConfigurationSection gamerulesSection) {
        for (String gameruleName : gamerulesSection.getKeys(false)) {
            String value = gamerulesSection.getString(gameruleName);
            if (!world.setGameRuleValue(gameruleName, value)) {
                Bukkit.getLogger().info("[WorldManager] Gamerule " + gameruleName + " für Welt " + world.getName() + " konnte nicht auf " + value + " gesetzt werden.");
            }
        }
    }
}
