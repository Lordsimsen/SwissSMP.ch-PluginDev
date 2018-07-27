package ch.swisssmp.dungeongenerator;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockVector;

public class GenerationRoutine implements Runnable {
	private final List<GenerationPart> generatables;
	private final BlockVector referencePoint;
	private int iterator = 0;
	private BukkitTask task;
	
	private GenerationRoutine(List<GenerationPart> generatables, BlockVector referencePoint){
		this.generatables = generatables;
		this.referencePoint = referencePoint;
	}

	@Override
	public void run() {
		if(iterator>=generatables.size()){
			task.cancel();
			Bukkit.getLogger().info("[DungeonGenerator] Dungeon Generation completed.");
			return;
		}
		generatables.get(iterator).generate(referencePoint);
		iterator++;
	}
	
	public static BukkitTask run(List<GenerationPart> generatables, BlockVector referencePoint){
		GenerationRoutine routine = new GenerationRoutine(generatables, referencePoint);
		BukkitTask result = Bukkit.getScheduler().runTaskTimer(DungeonGeneratorPlugin.plugin, routine, 0, 1);
		routine.task = result;
		return result;
	}
}
