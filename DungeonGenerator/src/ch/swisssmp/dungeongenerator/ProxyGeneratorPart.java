package ch.swisssmp.dungeongenerator;

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
		World world = original.getGenerator().getWorld();
		this.original = original;
		this.rotation = rotation;
		BlockVector min = original.getMinPoint();
		BlockVector max = original.getMaxPoint();
		BlockVector bottomMin = new BlockVector(min.getBlockX(), min.getBlockY()-1, min.getBlockZ());
		BlockVector bottomMax = new BlockVector(max.getBlockX(),min.getBlockY()-1, max.getBlockZ());
		BlockVector topMin = new BlockVector(min.getBlockX(), max.getBlockY()+1, min.getBlockZ());
		BlockVector topMax = new BlockVector(max.getBlockX(),max.getBlockY()+1, max.getBlockZ());
		this.topSignature = BlockUtil.getSignature(world, topMin, topMax, rotation);
		this.bottomSignature = BlockUtil.getSignature(world, bottomMin, bottomMax, rotation);
		this.northSignature = original.getSignature(Direction.NORTH.rotate(rotation));
		this.eastSignature = original.getSignature(Direction.EAST.rotate(rotation));
		this.southSignature = original.getSignature(Direction.SOUTH.rotate(rotation));
		this.westSignature = original.getSignature(Direction.WEST.rotate(rotation));
	}
	public DungeonGenerator getGenerator(){return this.original.getGenerator();}
	public void generate(BlockVector position){this.original.generate(position, rotation);}
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
	
	public GeneratorPart getOriginal(){
		return this.original;
	}
}
