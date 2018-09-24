package ch.swisssmp.dungeongenerator;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.util.BlockVector;

import ch.swisssmp.utils.ObservableRoutine;

public abstract class PartIteratorRoutine extends ObservableRoutine {
	protected final World world;
	private final List<GenerationPart> generatables;
	private final BlockVector referencePoint;
	private final int partSizeXZ;
	private final int partSizeY;
	private int iterator = 0;
	
	private GenerationPart part;
	
	protected PartIteratorRoutine(List<GenerationPart> generatables, World world, BlockVector referencePoint, int partSizeXZ, int partSizeY){
		this.generatables = generatables;
		this.world = world;
		this.referencePoint = referencePoint;
		this.partSizeXZ = partSizeXZ;
		this.partSizeY = partSizeY;
	}

	@Override
	public void run() {
		if(iterator>=generatables.size()){
			Bukkit.getLogger().info("[DungeonGenerator] Dungeon Generation completed.");
			this.iterator++;
			this.finish();
			return;
		}
		try{
			part = generatables.get(iterator);
			this.partIterator(part, GeneratorUtil.getWorldPosition(referencePoint, part.getGridPosition(), partSizeXZ, partSizeY));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		iterator++;
	}
	
	/**
	 * Returns the current progress
	 * @return A number representing the current progress between 0 and 1
	 */
	@Override
	public float getProgress(){
		return (float)this.iterator/this.generatables.size();
	}
	
	protected abstract void partIterator(GenerationPart part, BlockVector worldPosition);
}
