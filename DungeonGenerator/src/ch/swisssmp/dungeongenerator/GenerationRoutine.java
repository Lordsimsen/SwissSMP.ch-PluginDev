package ch.swisssmp.dungeongenerator;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockVector;

public class GenerationRoutine implements Runnable {
	private final List<GenerationPart> generatables;
	private final World world;
	private final BlockVector referencePoint;
	private int iterator = 0;
	private BukkitTask task;
	
	private GenerationRoutine(List<GenerationPart> generatables, World world, BlockVector referencePoint){
		this.generatables = generatables;
		this.world = world;
		this.referencePoint = referencePoint;
	}

	@Override
	public void run() {
		if(iterator>=generatables.size()){
			task.cancel();
			Bukkit.getLogger().info("[DungeonGenerator] Dungeon Generation completed.");
			return;
		}
		try{			
			generatables.get(iterator).generate(world, referencePoint);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		iterator++;
	}
	
	public static BukkitTask run(List<GenerationPart> generatables, World world, BlockVector referencePoint){
		GenerationRoutine routine = new GenerationRoutine(generatables, world, referencePoint);
		BukkitTask result = Bukkit.getScheduler().runTaskTimer(DungeonGeneratorPlugin.plugin, routine, 0, 1);
		routine.task = result;
		return result;
	}
}
