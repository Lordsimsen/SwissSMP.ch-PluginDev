package ch.swisssmp.dungeongenerator;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import ch.swisssmp.utils.ObservableRoutine;

public class PartGenerationRoutine extends ObservableRoutine {
	
	private final DungeonGenerator generator;
	private final CommandSender sender;
	private final long seed;
	private final int size;
	
	private final List<GenerationPart> result = new ArrayList<GenerationPart>();
	
	protected PartGenerationRoutine(DungeonGenerator generator, CommandSender sender, long seed, int size){
		this.generator = generator;
		this.sender = sender;
		this.seed = seed;
		this.size = size;
	}

	@Override
	public void run() {
		List<GenerationPart> parts = PartGenerator.generateData(sender, generator, generator.getGenerationPosition(), seed, size);
		if(parts==null){
			this.finish();
			return;
		}
		this.result.addAll(parts);
	}

	@Override
	public float getProgress() {
		return 1;
	}
	
	protected List<GenerationPart> getResult(){
		return this.result;
	}

	@Override
	public String getProgressLabel() {
		return "Zusammensetzen..";
	}

}
