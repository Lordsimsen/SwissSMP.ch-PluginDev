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
	private final Random random;
	
	private final int layer;
	
	private List<GenerationPart> otherNeighbours;

	private GenerationPart topPart;
	private GenerationPart bottomPart;
	private GenerationPart northPart;
	private GenerationPart eastPart;
	private GenerationPart southPart;
	private GenerationPart westPart;
	
	private String topSignature;
	private String bottomSignature;
	private String northSignature;
	private String eastSignature;
	private String southSignature;
	private String westSignature;

	private int distanceToStart;
	
	private Collection<ProxyGeneratorPart> invalidParts = new ArrayList<ProxyGeneratorPart>(); //parts that didn't work out and had to be removed
	
	
	protected GeneratorPartSelector(PartGenerator partGenerator, BlockVector gridPosition){
		this.partGenerator = partGenerator;
		this.gridPosition = gridPosition;
		this.random = partGenerator.getRandom();
		
		this.layer = gridPosition.getBlockY();
	}
	
	/**
	 * Selects a suitable part for its grid position based on the provided PartGenerationMode
	 * @param otherNeighbours - Used for part limit checks
	 * @param mode - The mode to select suitable parts
	 * @return A suitable GenerationPart if found;
	 * 	 	   <code>null</code> otherwise
	 */
	protected GenerationPart getPart(List<GenerationPart> otherNeighbours, PartGenerationMode mode){
		this.updateNeighbours();
		this.otherNeighbours = otherNeighbours;
		ArrayList<ProxyGeneratorPart> candidates = this.findSuitableParts(mode);
		if(candidates.size()==0 && (mode==PartGenerationMode.PREFER_CLOSED || mode==PartGenerationMode.PREFER_FILLED)){
			candidates = this.findSuitableParts(PartGenerationMode.FREE);
		}
		if(candidates.size()==0){
			//System.out.println("[DungeonGenerator] Could not find part at "+this.gridPosition.getBlockX()+", "+this.gridPosition.getBlockY()+", "+this.gridPosition.getBlockZ()+" ("+this.mode.toString()+") with signatures "+topSignature+", "+bottomSignature+", "+northSignature+", "+eastSignature+", "+southSignature+", "+westSignature);
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
	
	protected void addInvalidPart(ProxyGeneratorPart invalidPart){
		this.invalidParts.add(invalidPart);
	}
	
	private void updateNeighbours(){
		this.topPart = partGenerator.getPart(Direction.UP.moveVector(gridPosition));
		this.bottomPart = partGenerator.getPart(Direction.DOWN.moveVector(gridPosition));
		this.northPart = partGenerator.getPart(Direction.NORTH.moveVector(gridPosition));
		this.eastPart = partGenerator.getPart(Direction.EAST.moveVector(gridPosition));
		this.southPart = partGenerator.getPart(Direction.SOUTH.moveVector(gridPosition));
		this.westPart = partGenerator.getPart(Direction.WEST.moveVector(gridPosition));

		this.topSignature = topPart!=null ? topPart.getSignature(Direction.UP.opposite()) : null;
		this.bottomSignature = bottomPart!=null ? bottomPart.getSignature(Direction.DOWN.opposite()) : null;
		this.northSignature = northPart!=null ? northPart.getSignature(Direction.NORTH.opposite()) : null;
		this.eastSignature = eastPart!=null ? eastPart.getSignature(Direction.EAST.opposite()) : null;
		this.southSignature = southPart!=null ? southPart.getSignature(Direction.SOUTH.opposite()) : null;
		this.westSignature = westPart!=null ? westPart.getSignature(Direction.WEST.opposite()) : null;
		
		this.distanceToStart = GeneratorUtil.getDistanceToStart(this.topPart,this.bottomPart,this.northPart,this.eastPart,this.southPart,this.westPart);
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
		for(ProxyGeneratorPart part : this.partGenerator.getTemplateParts()){
			if(this.invalidParts.contains(part)) continue; //didn't work out in an earlier attempt
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
			if(!isSignatureMatch(part.getSignature(Direction.UP), this.topSignature, mode)) continue;
			if(!isSignatureMatch(part.getSignature(Direction.DOWN), this.bottomSignature, mode)) continue;
			if(!isSignatureMatch(part.getSignature(Direction.NORTH), this.northSignature, mode)) continue;
			if(!isSignatureMatch(part.getSignature(Direction.EAST), this.eastSignature, mode)) continue;
			if(!isSignatureMatch(part.getSignature(Direction.SOUTH), this.southSignature, mode)) continue;
			if(!isSignatureMatch(part.getSignature(Direction.WEST), this.westSignature, mode)) continue;
			result.add(part);
		}
		return result;
	}
	
	private boolean isSignatureMatch(String signature, String required, PartGenerationMode mode){
		if(mode==PartGenerationMode.FREE) return required==null || signature.equals(required);
		else if(mode==PartGenerationMode.PREFER_CLOSED || mode==PartGenerationMode.FORCE_CLOSE) return (required==null ? signature.isEmpty() : signature.equals(required));
		else if(mode==PartGenerationMode.PREFER_FILLED) return (required==null ? !signature.isEmpty() : signature.equals(required));
		else throw new NullPointerException("Unkown PartGenerationMode "+mode.toString());
	}
	
	private int getPartCount(GeneratorPart part){
		if(part==null) return 0;
		int result = this.partGenerator.getPartCount(part);
		for(GenerationPart otherNeighbour : this.otherNeighbours){
			if(otherNeighbour.getOriginal()==part) result++;
		}
		return result;
	}
}
