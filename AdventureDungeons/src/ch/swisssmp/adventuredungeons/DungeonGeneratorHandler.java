package ch.swisssmp.adventuredungeons;

import org.bukkit.Bukkit;
import org.bukkit.World;

import ch.swisssmp.dungeongenerator.DungeonGenerator;
import ch.swisssmp.dungeongenerator.GeneratorManager;

public class DungeonGeneratorHandler {
	protected static void generateDungeons(World world, String templateWorldName, long seed){
		GeneratorManager manager = GeneratorManager.get(world);
		for(DungeonGenerator generator : manager.getAll()){
			generator.generate(Bukkit.getConsoleSender(), seed);
		}
	}
}
