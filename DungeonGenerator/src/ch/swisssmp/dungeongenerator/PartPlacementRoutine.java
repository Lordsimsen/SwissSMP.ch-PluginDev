package ch.swisssmp.dungeongenerator;

import java.util.List;

import org.bukkit.World;
import org.bukkit.util.BlockVector;

public class PartPlacementRoutine extends PartIteratorRoutine {
	
	protected PartPlacementRoutine(List<GenerationPart> generatables, World world, BlockVector referencePoint, int partSizeXZ, int partSizeY){
		super(generatables, world, referencePoint, partSizeXZ, partSizeY);
	}

	@Override
	protected void partIterator(GenerationPart part, BlockVector worldPosition) {
		part.generate(world, worldPosition);
	}

	@Override
	public String getProgressLabel() {
		return "Dungeon wird generiert..";
	}
}
