package ch.swisssmp.dungeongenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.World;
import org.bukkit.util.BlockVector;

public class GenerationPart{
	private final ProxyGeneratorPart template;
	private final BlockVector gridPosition;
	
	private boolean staticPart = false;
	
	private GenerationPart topNeighbour;
	private GenerationPart bottomNeighbour;
	private GenerationPart northNeighbour;
	private GenerationPart eastNeighbour;
	private GenerationPart southNeighbour;
	private GenerationPart westNeighbour;
	
	private DungeonFloor floor;
	
	private HashMap<PartType,Integer> typeDistances = new HashMap<PartType,Integer>();
	
	protected GenerationPart(ProxyGeneratorPart template, BlockVector gridPosition, DungeonFloor floor){
		this.template = template;
		this.gridPosition = gridPosition;
		this.floor = floor;
	}
	
	/**
	 * Generates the blocks for this part
	 * @param world - The World to generate the blocks in
	 * @param position - The reference position to place the blocks
	 */
	protected void generate(World world, BlockVector position){
		this.template.generate(world, position);
	}
	
	protected void reset(World world, BlockVector position){
		this.template.reset(world, position);
	}
	
	protected void register(){
		if(topNeighbour!=null)topNeighbour.setNeighbour(this, Direction.UP.opposite());
		if(bottomNeighbour!=null)bottomNeighbour.setNeighbour(this, Direction.DOWN.opposite());
		if(northNeighbour!=null)northNeighbour.setNeighbour(this, Direction.NORTH.opposite());
		if(eastNeighbour!=null)eastNeighbour.setNeighbour(this, Direction.EAST.opposite());
		if(southNeighbour!=null)southNeighbour.setNeighbour(this, Direction.SOUTH.opposite());
		if(westNeighbour!=null)westNeighbour.setNeighbour(this, Direction.WEST.opposite());
		if(this.getPartTypes().contains(PartType.FORK)){
			this.floor.addFork();
		}
		if(this.getPartTypes().contains(PartType.CHAMBER)){
			for(GenerationPart neighbour : this.getNeighbours()){
				if(neighbour==null) continue;
				if(!neighbour.getPartTypes().contains(PartType.CHAMBER)) continue;
				this.floor.addChamber();
				break;
			}
		}
	}
	
	/**
	 * Removes this part as a neighbour from all surrounding parts
	 */
	protected void remove(){
		if(this.getPartTypes().contains(PartType.FORK)){
			this.floor.removeFork();
		}
		if(this.getPartTypes().contains(PartType.CHAMBER)){
			boolean chamberLeft = false;
			for(GenerationPart neighbour : this.getNeighbours()){
				if(neighbour==null) continue;
				if(!neighbour.getPartTypes().contains(PartType.CHAMBER)) continue;
				chamberLeft = true;
				break;
			}
			if(!chamberLeft) this.floor.removeChamber();
		}
		{
			GenerationPart neighbour;
			for(Direction direction : Direction.values()){
				neighbour = this.getNeighbour(direction);
				if(neighbour==null) continue;
				neighbour.setNeighbour(null, direction.opposite());
			}
		}
		for(GenerationPart neighbour : this.getNeighbours()){
			if(neighbour==null) continue;
			neighbour.updateDistances();
		}
	}

	/**
	 * DistanceToStart is the amount of GenerationParts you need to pass to get to the first GenerationPart placed
	 * Upon updating the distanceToStart the part will update all of its neighbours recursively if necessary
	 */
	protected void updateDistances(){
		for(PartType partType : PartType.values()){
			if(this.getPartTypes().contains(partType)){
				this.setDistance(partType, 0);
			}
			else{
				this.setDistance(partType, GeneratorUtil.getDistance(partType, this.topNeighbour,this.bottomNeighbour,this.northNeighbour,this.eastNeighbour,this.southNeighbour,this.westNeighbour));
			}
		}
	}
	
	/**
	 * DistanceToStart is the amount of GenerationParts you need to pass to get to the first GenerationPart placed
	 * Upon setting the distanceToStart the part will update all of its neighbours recursively if necessary
	 */
	protected void setDistance(PartType partType, int distance){
		this.typeDistances.put(partType, distance);
		for(GenerationPart neighbour : this.getNeighbours()){
			if(neighbour!=null && neighbour.getDistance(partType)>distance+1) neighbour.setDistance(partType, distance+1);
		}
	}

	/**
	 * DistanceToStart is the amount of GenerationParts you need to pass to get to the first GenerationPart placed
	 */
	protected int getDistance(PartType partType){
		return this.typeDistances.containsKey(partType) ? this.typeDistances.get(partType) : 0;
	}
	
	/**
	 * Checks which of its neighbours are valid and then returns a random one
	 */
	protected Collection<GenerationPart> getNeighbours(){
		ArrayList<GenerationPart> validNeighbours = new ArrayList<GenerationPart>();
		if(this.topNeighbour!=null) validNeighbours.add(this.topNeighbour);
		if(this.bottomNeighbour!=null) validNeighbours.add(this.bottomNeighbour);
		if(this.northNeighbour!=null) validNeighbours.add(this.northNeighbour);
		if(this.eastNeighbour!=null) validNeighbours.add(this.eastNeighbour);
		if(this.southNeighbour!=null) validNeighbours.add(this.southNeighbour);
		if(this.westNeighbour!=null) validNeighbours.add(this.westNeighbour);
		return validNeighbours;
	}
	
	protected Collection<PartType> getPartTypes(){
		return this.template.getPartTypes();
	}
	
	protected DungeonFloor getFloor(){
		return this.floor;
	}
	
	protected String getImage(){
		return this.template.getImage();
	}
	
	protected String getSignature(Direction direction){
		return this.template.getSignature(direction);
	}
	
	protected String getInfoString(){
		return this.template.getInfoString();
	}
	
	protected int getLimit(){
		return this.template.getLimit();
	}
	
	protected GeneratorPart getOriginal(){
		return this.template.getOriginal();
	}
	
	protected ProxyGeneratorPart getProxy(){
		return this.template;
	}
	public boolean typeMatch(PartType...partTypes){
		return this.template.typeMatch(partTypes);
	}

	protected BlockVector getGridPosition(){return this.gridPosition;}
	
	protected GenerationPart getNeighbour(Direction direction){
		switch(direction){
		case UP: return this.topNeighbour;
		case DOWN: return this.bottomNeighbour;
		case NORTH: return this.northNeighbour;
		case EAST: return this.eastNeighbour;
		case SOUTH: return this.southNeighbour;
		case WEST: return this.westNeighbour;
		default: return null;
		}
	}
	
	protected void setNeighbour(GenerationPart part, Direction direction){
		switch(direction){
		case UP: this.topNeighbour = part; break;
		case DOWN: this.bottomNeighbour = part; break;
		case NORTH: this.northNeighbour = part; break;
		case EAST: this.eastNeighbour = part; break;
		case SOUTH: this.southNeighbour = part; break;
		case WEST: this.westNeighbour = part; break;
		default: return;
		}
	}
	
	protected void setStatic(boolean staticPart){
		this.staticPart = staticPart;
	}
	
	protected boolean isStatic(){
		return this.staticPart;
	}
}
