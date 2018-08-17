package ch.swisssmp.adventuredungeons.world;

import java.util.Collection;

import org.bukkit.World;

import ch.swisssmp.dungeongenerator.DungeonGenerator;
import ch.swisssmp.dungeongenerator.GeneratorManager;

public class DungeonGeneratorHandler {
	protected static void generateDungeons(World world, String templateWorldName, long seed){
		GeneratorManager manager = GeneratorManager.get(world);
		Collection<String> generatorNames = manager.importAll(templateWorldName);
		DungeonGenerator generator;
		for(String generatorName : generatorNames){
			generator = manager.get(generatorName);
			if(generator==null) continue;
			generator.generate(world, seed);
		}
	}
}
