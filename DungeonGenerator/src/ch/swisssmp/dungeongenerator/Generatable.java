package ch.swisssmp.dungeongenerator;

import org.bukkit.util.BlockVector;

public interface Generatable {
	void generate(BlockVector referencePoint);
}
