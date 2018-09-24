package ch.swisssmp.dungeongenerator;

import java.util.Collection;
import java.util.List;

import org.bukkit.World;
import org.bukkit.util.BlockVector;

public class ProxyGeneratorPart {
	private final GeneratorPart original;
	private final int rotation;
	private final String topSignature;
	private final String bottomSignature;
	private final String northSignature;
	private final String eastSignature;
	private final String southSignature;
	private final String westSignature;
	
	public ProxyGeneratorPart(GeneratorPart original, int rotation){
		this.original = original;
		this.rotation = rotation;
		this.topSignature = original.getSignature(Direction.UP, rotation);
		this.bottomSignature = original.getSignature(Direction.DOWN, rotation);
		this.northSignature = original.getSignature(Direction.NORTH.rotate(rotation), rotation);
		this.eastSignature = original.getSignature(Direction.EAST.rotate(rotation), rotation);
		this.southSignature = original.getSignature(Direction.SOUTH.rotate(rotation), rotation);
		this.westSignature = original.getSignature(Direction.WEST.rotate(rotation), rotation);
	}
	public DungeonGenerator getGenerator(){return this.original.getGenerator();}
	public void generate(World world, BlockVector position){this.original.generate(world, position, rotation);}
	public void reset(World world, BlockVector position){this.original.reset(world, position);}
	public String getSignature(Direction direction){
		switch(direction){
		case UP: return this.topSignature;
		case DOWN: return this.bottomSignature;
		case NORTH: return this.northSignature;
		case EAST: return this.eastSignature;
		case SOUTH: return this.southSignature;
		case WEST: return this.westSignature;
		default: return null;
		}
	}
	public int getId(){
		return this.original.getId();
	}
	public String getInfoString(){
		return this.original.getInfoString();
	}
	public int getRotation(){
		return this.rotation;
	}
	public float getWeight(){
		return this.original.getWeight();
	}
	public int getLimit(){
		return this.original.getLimit();
	}
	public int getMinDistance(){
		return this.original.getMinDistance();
	}
	public int getMaxDistance(){
		return this.original.getMaxDistance();
	}
	public List<Integer> getLayers(){
		return this.original.getLayers();
	}
	public Collection<PartType> getPartTypes(){
		return this.original.getPartTypes();
	}
	public boolean typeMatch(PartType...partTypes){
		return this.original.typeMatch(partTypes);
	}
	public String getImage(){
		return this.original.getImage();
	}
	public GeneratorPart getOriginal(){
		return this.original;
	}
}
