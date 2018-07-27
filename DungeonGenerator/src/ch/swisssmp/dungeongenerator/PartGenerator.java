package ch.swisssmp.dungeongenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.util.BlockVector;

public class PartGenerator {
	private final DungeonGenerator generator;
	private final BlockVector startPosition;
	private final Random random;
	
	private final HashMap<BlockVector,GenerationPart> generationData = new HashMap<BlockVector,GenerationPart>(); //BlockVector represents grid coordinates, not block coordinates;
	private final HashMap<GeneratorPart,Integer> partCounts = new HashMap<GeneratorPart,Integer>();
	private final List<ProxyGeneratorPart> templateParts;
	private final ArrayList<GenerationPart> pending = new ArrayList<GenerationPart>();
	private final ArrayList<GenerationPart> invalidParts = new ArrayList<GenerationPart>();

	private int size;
	private int remainingCycles = 0;
	
	public PartGenerator(DungeonGenerator generator, BlockVector startPosition, int size, Long seed){
		this.generator = generator;
		this.startPosition = startPosition;
		this.size = size;
		this.random = new Random(seed);
		this.templateParts = this.generateRotatedTemplateVersions(generator.getTemplateParts());
	}
	
	/*
	 * Adds rotated proxy versions for all GeneratorParts
	 */
	private List<ProxyGeneratorPart> generateRotatedTemplateVersions(List<GeneratorPart> templateParts){
		List<ProxyGeneratorPart> result = new ArrayList<ProxyGeneratorPart>();
		for(GeneratorPart part : templateParts){
			result.add(new ProxyGeneratorPart(part, 0));
			result.add(new ProxyGeneratorPart(part, 1));
			result.add(new ProxyGeneratorPart(part, 2));
			result.add(new ProxyGeneratorPart(part, 3));
		}
		return result;
	}
	
	private ArrayList<Generatable> generateData(){
		GenerationPart part = new GenerationPart(templateParts.get(0), new BlockVector(0,0,0));
		this.pending.add(part);
		this.generationData.put(part.getGridPosition(), part);
		if(part.getTemplate().getOriginal().getLimit()>=0){
			this.partCounts.put(part.getTemplate().getOriginal(), 1);
		}
		part.setDistanceToStart(0);
		this.remainingCycles = size*2;
		PartGenerationMode mode = PartGenerationMode.FREE;
		boolean isValid;
		/*
		 * Creates parts until it is no longer possible
		 * As soon as remainingCycles is below 0 all new parts are preferrably closed
		 */
		while(pending.size()>0 && remainingCycles > -this.size*100){ //if remainingCycles is smaller than -100*size the system has failed that many times to fill unfinished parts, which is an indicator for an error in the process
			part = pending.get(0);
			isValid = this.generateMissingNeighbours(part, mode);
			if(!isValid) invalidParts.add(part);
			pending.remove(0);
			if(pending.size()==0){
				this.removeInvalidParts();
			}
			if(mode == PartGenerationMode.FREE && this.generationData.size()>=this.size){
				mode = PartGenerationMode.PREFER_CLOSED;
			}
			else if(mode == PartGenerationMode.PREFER_CLOSED && this.remainingCycles<0){
				mode = PartGenerationMode.FORCE_CLOSE;
			}
			remainingCycles--;
		}
		if(pending.size()>0){
			Bukkit.getLogger().info("[DungeonGenerator] "+pending.size()+" GenerationParts are still be pending.");
		}
		if(invalidParts.size()>0){
			Bukkit.getLogger().info("[DungeonGenerator] "+invalidParts.size()+" GenerationParts are invalid.");
		}
		return new ArrayList<Generatable>(generationData.values());
	}
	
	private boolean generateMissingNeighbours(GenerationPart part, PartGenerationMode mode){
		List<GenerationPart> neighbours = new ArrayList<GenerationPart>();
		{
			BlockVector partGridPosition = part.getGridPosition();
			BlockVector partWorldPosition = part.getWorldPosition(this.startPosition);
			BlockVector newPartGridPosition;
			GenerationPart newPart;
			boolean limitExpansion;
			for(Direction direction : Direction.values()){
				if(part.getNeighbour(direction)!=null || part.getTemplate().getSignature(direction).isEmpty()) continue; //nothing to do in this direction
				newPartGridPosition = direction.moveVector(partGridPosition);
				limitExpansion = this.isExpansionLimited(partWorldPosition, direction);
				newPart = GeneratorPartSelector.getPart(this, neighbours, newPartGridPosition, limitExpansion? PartGenerationMode.FORCE_CLOSE : mode);
				if(newPart!=null){
					neighbours.add(newPart);
				}
				else{
					//cancel generation of neighbours, the part will be removed in the invalid loop
					return false;
				}
			}
		}
		int count;
		GeneratorPart originalPart;
		for(GenerationPart newNeighbour : neighbours){
			newNeighbour.register();
			this.pending.add(newNeighbour);
			this.generationData.put(newNeighbour.getGridPosition(), newNeighbour);
			newNeighbour.updateDistanceToStart();
			originalPart = newNeighbour.getTemplate().getOriginal();
			if(originalPart.getLimit()>=0){
				count = this.getPartCount(originalPart);
				this.partCounts.put(originalPart, count+1);
			}
		}
		return true;
	}

	/*
	 * Removes invalid parts and closes their gaps with preferrably closed elements until all parts are valid
	 */
	private void removeInvalidParts(){
		GenerationPart part;
		GeneratorPart original;
		while(invalidParts.size()>0){
			part = invalidParts.get(0);
			generationData.remove(part.getGridPosition());
			for(GenerationPart neighbour : part.getNeighbours()){
				//the neighbour must not be pending and must not be flagged as invalid
				if(!this.pending.contains(neighbour) && !this.invalidParts.contains(neighbour)){
					this.pending.add(neighbour);
				}
			}
			invalidParts.remove(part);
			part.remove();
			original = part.getTemplate().getOriginal();
			if(partCounts.containsKey(original)){
				int count = this.getPartCount(original);
				partCounts.put(original, count-1);
			}
		}
	}
	
	private boolean isExpansionLimited(BlockVector position, Direction direction){
		switch(direction){
		case UP: return position.getBlockY()+this.generator.getPartSizeY()*2>250;
		case DOWN: return position.getBlockY()-this.generator.getPartSizeY()*2<6;
		default:return false;
		}
	}
	
	public GenerationPart getPart(BlockVector blockVector){
		return this.generationData.get(blockVector);
	}
	
	public List<ProxyGeneratorPart> getTemplateParts(){
		return this.templateParts;
	}
	
	public Random getRandom(){
		return this.random;
	}
	
	public int getPartCount(GeneratorPart part){
		if(this.partCounts.containsKey(part)) return this.partCounts.get(part);
		return 0;
	}
	
	/*
	 * Returns a List of positioned and rotated Generatable Parts that fit together and  make up a dungeon
	 * After this stage the dungeon is complete, but yet has to be placed into the world
	 */
	public static List<Generatable> generateData(DungeonGenerator generator, BlockVector startPosition, Long seed, int size){
		if(generator.getTemplateParts().size()==0) return null;
		PartGenerator partGenerator = new PartGenerator(generator, startPosition, size, seed);
		return partGenerator.generateData();
	}
}
