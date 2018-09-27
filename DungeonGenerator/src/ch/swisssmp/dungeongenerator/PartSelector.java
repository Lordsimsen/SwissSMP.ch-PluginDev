package ch.swisssmp.dungeongenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
//import org.bukkit.Bukkit;
import org.bukkit.util.BlockVector;

public class PartSelector {
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

	private HashMap<PartType,Integer> typeDistances = new HashMap<PartType,Integer>();
	private int corridorLength = -1;
	
	private Collection<ProxyGeneratorPart> invalidParts = new ArrayList<ProxyGeneratorPart>(); //parts that didn't work out and had to be removed
	
	private Map<ProxyGeneratorPart,String> rejected;
	
	protected PartSelector(PartGenerator partGenerator, BlockVector gridPosition){
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
	protected GenerationPart getPart(GenerationPart previous, List<GenerationPart> otherNeighbours, PartGenerationMode mode){
		this.updateNeighbours();
		this.otherNeighbours = otherNeighbours;
		Map<PartType,String> validPartTypes = this.getValidPartTypes(previous);
		ArrayList<ProxyGeneratorPart> candidates = this.findSuitableParts(mode, validPartTypes.keySet());
		if(candidates.size()==0){
			LogEntryPartSelection logEntry = new LogEntryPartSelection(gridPosition, previous.getFloor(), mode, typeDistances, validPartTypes, rejected, new ArrayList<ProxyGeneratorPart>(), null);
			logEntry.setCorridorLength(corridorLength);
			this.partGenerator.addLogEntry(logEntry);
			validPartTypes.put(PartType.CORRIDOR, "Korridor verlängern für mehr Platz");
			for(PartType previousType : previous.getPartTypes()){
				validPartTypes.put(previousType, "Vorheriges verlängern");
			}
			candidates = this.findSuitableParts(PartGenerationMode.FREE, validPartTypes.keySet());
		}
		if(candidates.size()==0){
			//System.out.println("[DungeonGenerator] Could not find part at "+this.gridPosition.getBlockX()+", "+this.gridPosition.getBlockY()+", "+this.gridPosition.getBlockZ()+" ("+this.mode.toString()+") with signatures "+topSignature+", "+bottomSignature+", "+northSignature+", "+eastSignature+", "+southSignature+", "+westSignature);
			LogEntryPartSelection logEntry = new LogEntryPartSelection(gridPosition, previous.getFloor(), mode, typeDistances, validPartTypes, rejected, new ArrayList<ProxyGeneratorPart>(), null);
			logEntry.setCorridorLength(corridorLength);
			this.partGenerator.addLogEntry(logEntry);
			return null;
		}
		ProxyGeneratorPart part = this.getRandomPart(candidates);
		if(part==null) return null;
		DungeonFloor nextFloor = this.getNextFloor(previous, part);
		GenerationPart result = new GenerationPart(part, gridPosition, nextFloor);
		result.setNeighbour(topPart, Direction.UP);
		result.setNeighbour(bottomPart, Direction.DOWN);
		result.setNeighbour(northPart, Direction.NORTH);
		result.setNeighbour(eastPart, Direction.EAST);
		result.setNeighbour(southPart, Direction.SOUTH);
		result.setNeighbour(westPart, Direction.WEST);
		LogEntryPartSelection logEntry = new LogEntryPartSelection(gridPosition, result.getFloor(), mode, typeDistances, validPartTypes, rejected, candidates, part);
		logEntry.setCorridorLength(corridorLength);
		this.partGenerator.addLogEntry(logEntry);
		return result;
	}
	
	private DungeonFloor getNextFloor(GenerationPart previous, ProxyGeneratorPart next){
		if(!previous.getPartTypes().contains(PartType.STAIRS) || next.getPartTypes().contains(PartType.STAIRS)) return previous.getFloor();
		List<GenerationPart> stairs = this.getSimilarAttachedParts(previous, PartType.STAIRS);
		GenerationPart otherStairsEnd = this.getClosestToType(stairs, PartType.START);
		DungeonFloor previousFloor = previous.getFloor();
		int currentY = this.gridPosition.getBlockY();
		int stairsStartY = otherStairsEnd.getGridPosition().getBlockY();
		if(currentY>stairsStartY) return this.partGenerator.getFloor(previousFloor.getFloorIndex()+1);
		if(currentY<stairsStartY) return this.partGenerator.getFloor(previousFloor.getFloorIndex()-1);
		return previousFloor;
	}
	
	private GenerationPart getClosestToType(List<GenerationPart> parts, PartType partType){
		GenerationPart closest = parts.get(0);
		int closestDistance = closest.getDistance(partType);
		int currentDistance;
		for(GenerationPart part : parts){
			currentDistance = part.getDistance(partType);
			if(currentDistance>=closestDistance) continue;
			closest = part;
			closestDistance = currentDistance;
		}
		return closest;
	}
	
	private List<GenerationPart> getSimilarAttachedParts(GenerationPart previous, PartType... partTypes){
		List<GenerationPart> checked = new ArrayList<GenerationPart>();
		List<GenerationPart> result = new ArrayList<GenerationPart>();
		List<GenerationPart> pending = new ArrayList<GenerationPart>();
		GenerationPart current;
		GenerationPart neighbour;
		int maxLoops = 10000;
		if(previous.typeMatch(partTypes)) result.add(previous);
		pending.add(previous);
		while(pending.size()>0 && maxLoops>0){
			current = pending.get(0);
			pending.remove(0);
			for(Direction direction : Direction.values()){
				neighbour = current.getNeighbour(direction);
				if(neighbour==null || checked.contains(neighbour)) continue;
				checked.add(neighbour);
				if(!neighbour.typeMatch(partTypes)) continue;
				result.add(neighbour);
				pending.add(neighbour);
			}
			maxLoops--;
		}
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
		
		this.updateDistances();
	}
	
	private void updateDistances(){
		for(PartType partType : PartType.values()){
			this.typeDistances.put(partType, GeneratorUtil.getDistance(partType, this.topPart,this.bottomPart,this.northPart,this.eastPart,this.southPart,this.westPart));
		}
	}
	
	private int getDistance(PartType partType){
		return this.typeDistances.containsKey(partType) ? this.typeDistances.get(partType) : 0;
	}
	
	private Map<PartType,String> getValidPartTypes(GenerationPart previous){
		Map<PartType,String> result = new HashMap<PartType,String>();
		int maxCorridorLength = this.partGenerator.getGenerator().getCorridorLength();
		int maxChamberCount = this.partGenerator.getGenerator().getChamberCount();
		int maxBranches = this.partGenerator.getGenerator().getBranchDensity();
		result.put(PartType.GENERIC, "Standard");
		if(previous.getPartTypes().contains(PartType.CHAMBER)){
			result.put(PartType.CHAMBER, "Kammer erweitern");
		}
		if(!previous.getPartTypes().contains(PartType.STAIRS) && previous.getFloor().getChamberCount()>=maxChamberCount){
			result.put(PartType.STAIRS, "Ebene abschliessen");
			return result;
		}
		else if(previous.getPartTypes().contains(PartType.STAIRS)){
			result.put(PartType.STAIRS, "Treppe erweitern");
			result.put(PartType.CORRIDOR, "Neue Ebene starten");
		}
		else{
			List<GenerationPart> corridor = this.getSimilarAttachedParts(previous, PartType.CORRIDOR, PartType.DOOR, PartType.FORK);
			System.out.println("Korridor Grösse: "+corridor.size());
			if(corridor.size()==0){
				this.corridorLength = 0;
				result.put(PartType.CORRIDOR, "Korridor starten");
			}
			else{
				GenerationPart closestToStart = this.getClosestToType(corridor, PartType.START);
				this.corridorLength = previous.getDistance(PartType.START)-closestToStart.getDistance(PartType.START)+1;
				this.partGenerator.addLogEntry(new LogEntryCorridorLengthCalculation(this.gridPosition, previous.getFloor(), corridor, closestToStart, this.corridorLength));
				if(corridorLength>=maxCorridorLength){
					result.put(PartType.CHAMBER, "Kammer setzen");
				}
				else{
					result.put(PartType.CORRIDOR, "Korridor erweitern");
				}
			}
		}
		if(result.containsKey(PartType.CHAMBER)){
			result.put(PartType.CORRIDOR, "Schliesse Kammer ab");
			result.put(PartType.DEAD_END, "Beende diese Verzweigung");
		}
		if(result.containsKey(PartType.CORRIDOR)){
			result.put(PartType.DOOR, "Variante von Korridor");
			if(previous.getFloor().getForkCount()<maxBranches) result.put(PartType.FORK, "Variante von Korridor");
		}
		return result;
	}
	
	private ProxyGeneratorPart getRandomPart(Collection<ProxyGeneratorPart> parts){
		float weightSum = 0;
		for(ProxyGeneratorPart part : parts){
			weightSum+=(part.getWeight()>=0) ? part.getWeight() : 1;
		}
		double randomValue = this.random.nextDouble()*weightSum;
		float weight;
		for(ProxyGeneratorPart part : parts){
			weight = (part.getWeight()>=0?part.getWeight() : 1);
			if(weight>randomValue) return part;
			randomValue-=weight;
		}
		return null;
	}
	
	private ArrayList<ProxyGeneratorPart> findSuitableParts(PartGenerationMode mode, Collection<PartType> validPartTypes){
		this.rejected = new HashMap<ProxyGeneratorPart,String>();
		ArrayList<ProxyGeneratorPart> result = new ArrayList<ProxyGeneratorPart>();
		int distanceToStart = this.getDistance(PartType.START);
		ArrayList<PartType> matchingPartTypes;
		for(ProxyGeneratorPart part : this.partGenerator.getPartCollection()){
			if(this.invalidParts.contains(part)) continue; //didn't work out in an earlier attempt
			if(part.getLimit()>=0){
				rejected.put(part, "Max "+part.getLimit()+" Instanzen");
				if(this.getPartCount(part.getOriginal())>=part.getLimit()) continue;
			}
			if(part.getMinDistance()>=0 && distanceToStart<part.getMinDistance()){
				rejected.put(part, "Min "+part.getMinDistance()+" Teile zum Start");
				continue;
			}
			if(part.getMaxDistance()>=0 && distanceToStart>part.getMaxDistance()){
				rejected.put(part, "Max "+part.getMinDistance()+" Teile zum Start");
				continue;
			}
			if(part.getLayers()!=null && part.getLayers().size()>0 && !part.getLayers().contains(this.layer)){
				rejected.put(part, "Nur Layer "+StringUtils.join(part.getLayers(), ", "));
				continue;
			}
			matchingPartTypes = new ArrayList<PartType>(part.getPartTypes());
			matchingPartTypes.retainAll(validPartTypes);
			if(matchingPartTypes.size()==0){
				rejected.put(part, "Ist nicht "+StringUtils.join(validPartTypes, ", "));
				continue;
			}
			if(!isSignatureMatch(part.getSignature(Direction.UP), this.topSignature, mode)){
				rejected.put(part, "Top Signatur falsch: "+part.getSignature(Direction.UP));
				continue;
			}
			if(!isSignatureMatch(part.getSignature(Direction.DOWN), this.bottomSignature, mode)){
				rejected.put(part, "Top Signatur falsch: "+part.getSignature(Direction.DOWN));
				continue;
			}
			if(!isSignatureMatch(part.getSignature(Direction.NORTH), this.northSignature, mode)){
				rejected.put(part, "Nord Signatur falsch: "+part.getSignature(Direction.NORTH));
				continue;
			}
			if(!isSignatureMatch(part.getSignature(Direction.EAST), this.eastSignature, mode)){
				rejected.put(part, "Ost Signatur falsch: "+part.getSignature(Direction.EAST));
				continue;
			}
			if(!isSignatureMatch(part.getSignature(Direction.SOUTH), this.southSignature, mode)){
				rejected.put(part, "Süd Signatur falsch: "+part.getSignature(Direction.SOUTH));
				continue;
			}
			if(!isSignatureMatch(part.getSignature(Direction.WEST), this.westSignature, mode)){
				rejected.put(part, "West Signatur falsch: "+part.getSignature(Direction.WEST));
				continue;
			}
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
