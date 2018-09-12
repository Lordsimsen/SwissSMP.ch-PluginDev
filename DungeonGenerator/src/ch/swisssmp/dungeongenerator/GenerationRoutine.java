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
	private final int partSizeXZ;
	private final int partSizeY;
	private int iterator = 0;
	private BukkitTask task;
	
	private GenerationPart part;
	
	private GenerationRoutine(List<GenerationPart> generatables, World world, BlockVector referencePoint, int partSizeXZ, int partSizeY){
		this.generatables = generatables;
		this.world = world;
		this.referencePoint = referencePoint;
		this.partSizeXZ = partSizeXZ;
		this.partSizeY = partSizeY;
	}

	@Override
	public void run() {
		if(iterator>=generatables.size()){
			task.cancel();
			Bukkit.getLogger().info("[DungeonGenerator] Dungeon Generation completed.");
			return;
		}
		try{
			part = generatables.get(iterator);
			part.generate(world, GeneratorUtil.getWorldPosition(referencePoint, part.getGridPosition(), partSizeXZ, partSizeY));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		iterator++;
	}
	
	public static BukkitTask run(List<GenerationPart> generatables, World world, BlockVector referencePoint, int partSizeXZ, int partSizeY){
		GenerationRoutine routine = new GenerationRoutine(generatables, world, referencePoint, partSizeXZ, partSizeY);
		BukkitTask result = Bukkit.getScheduler().runTaskTimer(DungeonGeneratorPlugin.plugin, routine, 0, 1);
		routine.task = result;
		return result;
	}
}
