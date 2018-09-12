package ch.swisssmp.dungeongenerator;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.World;
import org.bukkit.util.BlockVector;

public class GenerationPart{
	private final ProxyGeneratorPart template;
	private final BlockVector gridPosition;
	
	private int distanceToStart = -1;
	
	private GenerationPart topNeighbour;
	private GenerationPart bottomNeighbour;
	private GenerationPart northNeighbour;
	private GenerationPart eastNeighbour;
	private GenerationPart southNeighbour;
	private GenerationPart westNeighbour;
	
	public GenerationPart(ProxyGeneratorPart template, BlockVector gridPosition){
		this.template = template;
		this.gridPosition = gridPosition;
	}
	
	/**
	 * Generates the blocks for this part
	 * @param world - The World to generate the blocks in
	 * @param position - The reference position to place the blocks
	 */
	public void generate(World world, BlockVector position){
		this.template.generate(world, position);
	}
	
	public void register(){
		if(topNeighbour!=null)topNeighbour.setNeighbour(this, Direction.UP.opposite());
		if(bottomNeighbour!=null)bottomNeighbour.setNeighbour(this, Direction.DOWN.opposite());
		if(northNeighbour!=null)northNeighbour.setNeighbour(this, Direction.NORTH.opposite());
		if(eastNeighbour!=null)eastNeighbour.setNeighbour(this, Direction.EAST.opposite());
		if(southNeighbour!=null)southNeighbour.setNeighbour(this, Direction.SOUTH.opposite());
		if(westNeighbour!=null)westNeighbour.setNeighbour(this, Direction.WEST.opposite());
	}
	
	/**
	 * Removes this part as a neighbour from all surrounding parts
	 */
	public void remove(){
		if(this.topNeighbour!=null) this.topNeighbour.setNeighbour(null, Direction.UP.opposite());
		if(this.bottomNeighbour!=null) this.bottomNeighbour.setNeighbour(null, Direction.DOWN.opposite());
		if(this.northNeighbour!=null) this.northNeighbour.setNeighbour(null, Direction.NORTH.opposite());
		if(this.eastNeighbour!=null) this.eastNeighbour.setNeighbour(null, Direction.EAST.opposite());
		if(this.southNeighbour!=null) this.southNeighbour.setNeighbour(null, Direction.SOUTH.opposite());
		if(this.westNeighbour!=null) this.westNeighbour.setNeighbour(null, Direction.WEST.opposite());
	}

	/**
	 * DistanceToStart is the amount of GenerationParts you need to pass to get to the first GenerationPart placed
	 * Upon updating the distanceToStart the part will update all of its neighbours recursively if necessary
	 */
	public void updateDistanceToStart(){
		this.setDistanceToStart(GeneratorUtil.getDistanceToStart(this.topNeighbour,this.bottomNeighbour,this.northNeighbour,this.eastNeighbour,this.southNeighbour,this.westNeighbour));
	}
	
	/**
	 * DistanceToStart is the amount of GenerationParts you need to pass to get to the first GenerationPart placed
	 * Upon setting the distanceToStart the part will update all of its neighbours recursively if necessary
	 */
	public void setDistanceToStart(int distanceToStart){
		this.distanceToStart = distanceToStart;
		if(this.topNeighbour!=null && this.topNeighbour.getDistanceToStart()>this.distanceToStart+1) this.topNeighbour.setDistanceToStart(distanceToStart+1);
		if(this.bottomNeighbour!=null && this.bottomNeighbour.getDistanceToStart()>this.distanceToStart+1) this.bottomNeighbour.setDistanceToStart(distanceToStart+1);
		if(this.northNeighbour!=null && this.northNeighbour.getDistanceToStart()>this.distanceToStart+1) this.northNeighbour.setDistanceToStart(distanceToStart+1);
		if(this.eastNeighbour!=null && this.eastNeighbour.getDistanceToStart()>this.distanceToStart+1) this.eastNeighbour.setDistanceToStart(distanceToStart+1);
		if(this.southNeighbour!=null && this.southNeighbour.getDistanceToStart()>this.distanceToStart+1) this.southNeighbour.setDistanceToStart(distanceToStart+1);
		if(this.westNeighbour!=null && this.westNeighbour.getDistanceToStart()>this.distanceToStart+1) this.westNeighbour.setDistanceToStart(distanceToStart+1);
	}

	/**
	 * DistanceToStart is the amount of GenerationParts you need to pass to get to the first GenerationPart placed
	 */
	public int getDistanceToStart(){
		return this.distanceToStart;
	}
	
	/**
	 * Checks which of its neighbours are valid and then returns a random one
	 */
	public Collection<GenerationPart> getNeighbours(){
		ArrayList<GenerationPart> validNeighbours = new ArrayList<GenerationPart>();
		if(this.topNeighbour!=null) validNeighbours.add(this.topNeighbour);
		if(this.bottomNeighbour!=null) validNeighbours.add(this.bottomNeighbour);
		if(this.northNeighbour!=null) validNeighbours.add(this.northNeighbour);
		if(this.eastNeighbour!=null) validNeighbours.add(this.eastNeighbour);
		if(this.southNeighbour!=null) validNeighbours.add(this.southNeighbour);
		if(this.westNeighbour!=null) validNeighbours.add(this.westNeighbour);
		return validNeighbours;
	}
	
	public String getSignature(Direction direction){
		return this.template.getSignature(direction);
	}
	
	public int getLimit(){
		return this.template.getLimit();
	}
	
	public GeneratorPart getOriginal(){
		return this.template.getOriginal();
	}
	
	public ProxyGeneratorPart getProxy(){
		return this.template;
	}

	public BlockVector getGridPosition(){return this.gridPosition;}
	
	public GenerationPart getNeighbour(Direction direction){
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
	
	public void setNeighbour(GenerationPart part, Direction direction){
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
}
