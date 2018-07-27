package ch.swisssmp.dungeongenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

//import org.bukkit.Bukkit;
import org.bukkit.util.BlockVector;

public class GeneratorPartSelector {
	private final PartGenerator partGenerator;
	private final BlockVector gridPosition;
	private final PartGenerationMode mode;
	private final Random random;
	
	private final int distanceToStart;
	private final int layer;
	
	private final List<GenerationPart> otherNeighbours;

	private final GenerationPart topPart;
	private final GenerationPart bottomPart;
	private final GenerationPart northPart;
	private final GenerationPart eastPart;
	private final GenerationPart southPart;
	private final GenerationPart westPart;
	
	private String topSignature;
	private String bottomSignature;
	private String northSignature;
	private String eastSignature;
	private String southSignature;
	private String westSignature;
	
	public GeneratorPartSelector(PartGenerator partGenerator, List<GenerationPart> otherNeighbours, BlockVector gridPosition, PartGenerationMode mode){
		this.partGenerator = partGenerator;
		this.gridPosition = gridPosition;
		this.mode = mode;
		this.random = partGenerator.getRandom();
		
		this.otherNeighbours = otherNeighbours;
		
		this.topPart = partGenerator.getPart(Direction.UP.moveVector(gridPosition));
		this.bottomPart = partGenerator.getPart(Direction.DOWN.moveVector(gridPosition));
		this.northPart = partGenerator.getPart(Direction.NORTH.moveVector(gridPosition));
		this.eastPart = partGenerator.getPart(Direction.EAST.moveVector(gridPosition));
		this.southPart = partGenerator.getPart(Direction.SOUTH.moveVector(gridPosition));
		this.westPart = partGenerator.getPart(Direction.WEST.moveVector(gridPosition));
		
		this.distanceToStart = GeneratorUtil.getDistanceToStart(this.topPart,this.bottomPart,this.northPart,this.eastPart,this.southPart,this.westPart);
		this.layer = gridPosition.getBlockY();
	}
	
	private GenerationPart getPart(){
		ArrayList<ProxyGeneratorPart> candidates = this.findSuitableParts(this.mode);
		if(candidates.size()==0 && this.mode==PartGenerationMode.PREFER_CLOSED){
			candidates = this.findSuitableParts(PartGenerationMode.FREE);
		}
		if(candidates.size()==0){
			//Bukkit.getLogger().info("[DungeonGenerator] Could not find part"+(preferClosed?" (preferrably closed)":"")+" with signatures "+topSignature+", "+bottomSignature+", "+northSignature+", "+eastSignature+", "+southSignature+", "+westSignature);
			return null;
		}
		ProxyGeneratorPart part = this.getRandomPart(candidates);
		if(part==null) return null;
		GenerationPart result = new GenerationPart(part, gridPosition);
		result.setNeighbour(topPart, Direction.UP);
		result.setNeighbour(bottomPart, Direction.DOWN);
		result.setNeighbour(northPart, Direction.NORTH);
		result.setNeighbour(eastPart, Direction.EAST);
		result.setNeighbour(southPart, Direction.SOUTH);
		result.setNeighbour(westPart, Direction.WEST);
		return result;
	}
	
	private ProxyGeneratorPart getRandomPart(Collection<ProxyGeneratorPart> parts){
		float weightSum = 0;
		float averageWeight = 1f/parts.size();
		for(ProxyGeneratorPart part : parts){
			weightSum+=(part.getWeight()>=0) ? part.getWeight() : averageWeight;
		}
		double randomValue = this.random.nextDouble()*weightSum;
		float weight;
		for(ProxyGeneratorPart part : parts){
			weight = (part.getWeight()>=0?part.getWeight():averageWeight);
			if(weight>randomValue) return part;
			randomValue-=weight;
		}
		return null;
	}
	
	private ArrayList<ProxyGeneratorPart> findSuitableParts(PartGenerationMode mode){
		ArrayList<ProxyGeneratorPart> result = new ArrayList<ProxyGeneratorPart>();
		this.topSignature = topPart!=null ? topPart.getTemplate().getSignature(Direction.UP.opposite()) : (mode!=PartGenerationMode.FREE ?"":null);
		this.bottomSignature = bottomPart!=null ? bottomPart.getTemplate().getSignature(Direction.DOWN.opposite()) : (mode!=PartGenerationMode.FREE ?"":null);
		this.northSignature = northPart!=null ? northPart.getTemplate().getSignature(Direction.NORTH.opposite()) : (mode!=PartGenerationMode.FREE ?"":null);
		this.eastSignature = eastPart!=null ? eastPart.getTemplate().getSignature(Direction.EAST.opposite()) : (mode!=PartGenerationMode.FREE ?"":null);
		this.southSignature = southPart!=null ? southPart.getTemplate().getSignature(Direction.SOUTH.opposite()) : (mode!=PartGenerationMode.FREE ?"":null);
		this.westSignature = westPart!=null ? westPart.getTemplate().getSignature(Direction.WEST.opposite()) : (mode!=PartGenerationMode.FREE ?"":null);
		for(ProxyGeneratorPart part : this.partGenerator.getTemplateParts()){
			if(part.getLimit()>=0){
				if(this.getPartCount(part.getOriginal())>=part.getLimit()) continue;
			}
			if(part.getMinDistance()>=0){
				if(this.distanceToStart<part.getMinDistance()) continue;
			}
			if(part.getMaxDistance()>=0){
				if(this.distanceToStart>part.getMaxDistance()) continue;
			}
			if(part.getLayers()!=null && part.getLayers().size()>0){
				if(!part.getLayers().contains(this.layer)) continue;
			}
			if(topSignature!=null && !part.getSignature(Direction.UP).equals(topSignature)) continue;
			if(bottomSignature!=null && !part.getSignature(Direction.DOWN).equals(bottomSignature)) continue;
			if(northSignature!=null && !part.getSignature(Direction.NORTH).equals(northSignature)) continue;
			if(eastSignature!=null && !part.getSignature(Direction.EAST).equals(eastSignature)) continue;
			if(southSignature!=null && !part.getSignature(Direction.SOUTH).equals(southSignature)) continue;
			if(westSignature!=null && !part.getSignature(Direction.WEST).equals(westSignature)) continue;
			result.add(part);
		}
		return result;
	}
	
	private int getPartCount(GeneratorPart part){
		int result = this.partGenerator.getPartCount(part);
		for(GenerationPart otherNeighbour : this.otherNeighbours){
			if(otherNeighbour.getTemplate().getOriginal()==part) result++;
		}
		return result;
	}
	
	public static GenerationPart getPart(PartGenerator partGenerator, List<GenerationPart> otherNeighbours, BlockVector gridPosition, PartGenerationMode mode){
		GeneratorPartSelector search = new GeneratorPartSelector(partGenerator, otherNeighbours, gridPosition, mode);
		return search.getPart();
	}
}
