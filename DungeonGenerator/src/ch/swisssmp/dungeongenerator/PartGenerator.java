package ch.swisssmp.dungeongenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.util.BlockVector;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.webcore.DataSource;

public class PartGenerator {
	private final DungeonGenerator generator;
	private final BlockVector startPosition;
	private final Random random;
	
	private final HashMap<BlockVector,GenerationPart> generationData = new HashMap<BlockVector,GenerationPart>(); //BlockVector represents grid coordinates, not block coordinates;
	private final HashMap<BlockVector,PartSelector> partSelectors = new HashMap<BlockVector,PartSelector>();
	private final HashMap<GeneratorPart,Integer> partCounts = new HashMap<GeneratorPart,Integer>();
	private final PartCollection partCollection;
	private final ArrayList<GenerationPart> pending = new ArrayList<GenerationPart>();
	private final ArrayList<GenerationPart> invalidParts = new ArrayList<GenerationPart>();
	
	private final HashMap<Integer, DungeonFloor> floors = new HashMap<Integer,DungeonFloor>();

	private int size;
	private int obstructedPartsCount = 0;
	private int remainingCycles = 0;
	
	private List<LogEntry> log = new ArrayList<LogEntry>();
	
	private PartGenerator(DungeonGenerator generator, BlockVector startPosition, int size, Long seed){
		this.generator = generator;
		this.startPosition = startPosition;
		this.size = size;
		this.random = new Random(seed);
		this.partCollection = PartCollection.get(generator);
	}
	
	private ArrayList<GenerationPart> generateData(CommandSender sender){
		DungeonFloor floor = this.getFloor(0);
		this.generateObstructedParts(new BlockVector(0,0,0), floor);
		PartSelector partSelector = this.getPartSelector(new BlockVector(0,0,0));
		GenerationPart part = new GenerationPart(this.partCollection.get(0), new BlockVector(0,0,0), floor);
		part.setStatic(true);
		part.register();
		this.log.add(new LogEntryPartAdded(part));
		Bukkit.getLogger().info(part.getInfoString());
		this.pending.add(part);
		this.generationData.put(part.getGridPosition(), part);
		this.partSelectors.put(new BlockVector(0,0,0), partSelector);
		if(part.getLimit()>=0){
			this.partCounts.put(part.getOriginal(), 1);
		}
		part.updateDistances();
		this.remainingCycles = size*5;
		PartGenerationMode mode = PartGenerationMode.FREE;
		boolean isValid;
		/*
		 * Creates parts until it is no longer possible
		 * As soon as remainingCycles is below 0 all new parts are preferrably closed
		 */
		while(pending.size()>0 && remainingCycles > 0){ //if remainingCycles is smaller than -100*size the system has failed that many times to fill unfinished parts, which is an indicator for an error in the process
			part = pending.get(0);
			this.generateObstructedParts(part.getGridPosition(), part.getFloor());
			isValid = this.generateMissingNeighbours(part, mode);
			if(!isValid){
				if(part.isStatic()){
					sender.sendMessage("[DungeonGenerator] Generierung abgebrochen. Eine unmögliche Konstellation ist aufgetreten.");
					this.uploadLog();
					return null;
				}
				invalidParts.add(part);
			}
			pending.remove(0);
			if(pending.size()==0){
				this.removeInvalidParts();
			}
			if(mode == PartGenerationMode.FREE && this.generationData.size()-obstructedPartsCount>=this.size){
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
		Bukkit.getLogger().info("[DungeonGenerator] Generated Dungeon with "+generationData.size()+" parts.");
		this.uploadLog();
		return new ArrayList<GenerationPart>(generationData.values());
	}
	
	/**
	 * Puts ObstructedGenerationParts where Blocks are in the generator's way
	 * @param gridPosition - The position in the generation grid
	 */
	private void generateObstructedParts(BlockVector gridPosition, DungeonFloor floor){
		World world = this.generator.getWorld();
		BlockVector neighbourPosition;
		BlockVector neighboursNeighbourGridPosition;
		BlockVector neighboursNeighbourWorldPosition;
		Block block;
		ObstructedGenerationPart obstructedPart;
		for(Direction centerDirection : Direction.values()){
			neighbourPosition = centerDirection.moveVector(gridPosition);
			if(this.generationData.containsKey(neighbourPosition)) continue; //part already generated
			for(Direction neighbourDirection : Direction.values()){
				neighboursNeighbourGridPosition = neighbourDirection.moveVector(neighbourPosition);
				if(this.generationData.containsKey(neighboursNeighbourGridPosition)) continue; //part already generated
				neighboursNeighbourWorldPosition = this.getWorldPosition(neighboursNeighbourGridPosition);
				block = world.getBlockAt(neighboursNeighbourWorldPosition.getBlockX(), neighboursNeighbourWorldPosition.getBlockY(), neighboursNeighbourWorldPosition.getBlockZ());
				if(GeneratorUtil.isVolumeEmpty(block, generator.getPartSizeXZ(), generator.getPartSizeY(), this.generator.getGenerationBoxMaterial())) continue; //volume is empty
				obstructedPart = new ObstructedGenerationPart(neighboursNeighbourGridPosition, floor, this.generator.getPartSizeXZ(), this.generator.getPartSizeY());
				obstructedPart.findNeighbours(this.generationData);
				obstructedPart.register();
				obstructedPart.setStatic(true);
				this.addLogEntry(new LogEntryObstruction(obstructedPart));
				this.generationData.put(obstructedPart.getGridPosition(), obstructedPart); //mark position as obstructed
				obstructedPartsCount++;
			}
		}
	}
	
	private boolean generateMissingNeighbours(GenerationPart part, PartGenerationMode mode){
		List<GenerationPart> newNeighbours = new ArrayList<GenerationPart>();
		{
			BlockVector gridPosition = part.getGridPosition();
			BlockVector worldPosition = this.getWorldPosition(gridPosition);
			BlockVector newPartGridPosition;
			GenerationPart newPart;
			PartSelector partSelector;
			boolean limitExpansion;
			for(Direction direction : Direction.values()){
				if(part.getNeighbour(direction)!=null || part.getSignature(direction).isEmpty()) continue; //nothing to do in this direction
				newPartGridPosition = direction.moveVector(gridPosition);
				limitExpansion = this.isExpansionLimited(worldPosition, direction);
				partSelector = this.getPartSelector(newPartGridPosition);
				newPart = partSelector.getPart(part, newNeighbours, limitExpansion? PartGenerationMode.FORCE_CLOSE : mode);
				if(newPart!=null){
					newNeighbours.add(newPart);
				}
				else{
					//cancel generation of neighbours, the part will be removed in the invalid loop
					return false;
				}
			}
		}
		int count;
		for(GenerationPart newNeighbour : newNeighbours){
			if(this.generationData.containsKey(newNeighbour.getGridPosition())){
				System.out.println("Something is fishy.");
				continue;
			}
			newNeighbour.register();
			this.addLogEntry(new LogEntryPartAdded(newNeighbour));
			this.pending.add(newNeighbour);
			this.generationData.put(newNeighbour.getGridPosition(), newNeighbour);
			newNeighbour.updateDistances();
			if(newNeighbour.getLimit()>=0){
				count = this.getPartCount(newNeighbour.getOriginal());
				this.partCounts.put(newNeighbour.getOriginal(), count+1);
			}
		}
		return true;
	}

	/**
	 * Removes invalid parts and closes their gaps with preferrably closed elements until all parts are valid
	 */
	private void removeInvalidParts(){
		GenerationPart part;
		GeneratorPart original;
		PartSelector partSelector;
		while(invalidParts.size()>0){
			part = invalidParts.get(0);
			partSelector = this.partSelectors.get(part.getGridPosition());
			//System.out.println("[DungeonGenerator] Removing part at "+part.getGridPosition().getBlockX()+", "+part.getGridPosition().getBlockY()+", "+part.getGridPosition().getBlockZ());
			if(part instanceof ObstructedGenerationPart){
				System.out.println("[DungeonGenerator] Trying to remove ObstructedGenerationPart!!");
			}
			partSelector.addInvalidPart(part.getProxy());
			generationData.remove(part.getGridPosition());
			for(GenerationPart neighbour : part.getNeighbours()){
				//the neighbour must not be pending and must not be flagged as invalid
				if(!this.pending.contains(neighbour) && !this.invalidParts.contains(neighbour)){
					this.pending.add(neighbour);
				}
			}
			invalidParts.remove(part);
			part.remove();
			this.addLogEntry(new LogEntryPartRemoved(part));
			original = part.getOriginal();
			if(original!=null && partCounts.containsKey(original)){
				int count = this.getPartCount(original);
				partCounts.put(original, count-1);
			}
		}
	}
	
	private boolean isExpansionLimited(BlockVector worldPosition, Direction direction){
		if(direction==Direction.UP && worldPosition.getBlockY()+this.generator.getPartSizeY()*2>250) return true;
		if(direction==Direction.DOWN && worldPosition.getBlockY()-this.generator.getPartSizeY()*2<6) return true;
		return false;
	}
	
	private PartSelector getPartSelector(BlockVector gridPosition){
		if(this.partSelectors.containsKey(gridPosition)){
			return this.partSelectors.get(gridPosition);
		}
		PartSelector partSelector = new PartSelector(this,gridPosition);
		this.partSelectors.put(gridPosition, partSelector);
		return partSelector;
	}
	
	private void uploadLog(){
		Bukkit.getLogger().info("[DungeonGenerator] Log Grösse: "+log.size()+" Einträge");
		List<String> arguments = new ArrayList<String>();
		arguments.add("id="+this.generator.getId());
		arguments.add("generator_name="+URLEncoder.encode(this.generator.getName()));
		arguments.add("mc_world="+URLEncoder.encode(this.generator.getWorld().getName()));
		arguments.add("generation_position[x]="+this.startPosition.getBlockX());
		arguments.add("generation_position[y]="+this.startPosition.getBlockY());
		arguments.add("generation_position[z]="+this.startPosition.getBlockZ());
		arguments.add("configuration[size]="+this.generator.getDefaultSize());
		arguments.add("configuration[corridor_length]="+this.generator.getCorridorLength());
		arguments.add("configuration[chamber_count]="+this.generator.getChamberCount());
		arguments.add("configuration[branch_density]="+this.generator.getBranchDensity());
		JsonObject jsonData = new JsonObject();
		JsonArray logData = new JsonArray();
		for(LogEntry logEntry : this.log){
			logData.add(logEntry.getLogData());
		}
		jsonData.add("log_entries", logData);
		arguments.add("log="+Base64.encodeBase64URLSafeString(jsonData.toString().getBytes()));
		String[] argumentsArray = new String[arguments.size()];
		arguments.toArray(argumentsArray);
		Thread thread = new Thread(()->{
			DataSource.getResponse("dungeons/upload_generator_log.php",argumentsArray);
		});
		thread.start();
	}
	
	protected void addLogEntry(LogEntry logEntry){
		this.log.add(logEntry);
	}
	
	protected GenerationPart getPart(BlockVector blockVector){
		return this.generationData.get(blockVector);
	}
	
	protected PartCollection getPartCollection(){
		return this.partCollection;
	}
	
	protected Random getRandom(){
		return this.random;
	}
	
	protected BlockVector getWorldPosition(BlockVector gridPosition){
		return GeneratorUtil.getWorldPosition(this.startPosition, gridPosition, this.generator.getPartSizeXZ(), this.generator.getPartSizeY());
	}
	
	protected int getPartCount(GeneratorPart part){
		if(this.partCounts.containsKey(part)) return this.partCounts.get(part);
		return 0;
	}
	
	protected DungeonFloor getFloor(int index){
		if(this.floors.containsKey(index)) return this.floors.get(index);
		DungeonFloor result = new DungeonFloor(index);
		this.floors.put(index, result);
		return result;
	}
	
	protected DungeonGenerator getGenerator(){
		return this.generator;
	}
	
	/*
	 * Returns a List of positioned and rotated Generatable Parts that fit together and  make up a dungeon
	 * After this stage the dungeon is complete, but yet has to be placed into the world
	 */
	public static List<GenerationPart> generateData(CommandSender sender, DungeonGenerator generator, BlockVector startPosition, Long seed, int size){
		if(generator.getTemplateParts().size()==0){
			sender.sendMessage("[DungeonGenerator] Der Generator hat keine gültige Vorlage.");
			return null;
		}
		PartGenerator partGenerator = new PartGenerator(generator, startPosition, size, seed);
		return partGenerator.generateData(sender);
	}
}
