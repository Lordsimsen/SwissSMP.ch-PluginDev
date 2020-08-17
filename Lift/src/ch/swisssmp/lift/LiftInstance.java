package ch.swisssmp.lift;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BoundingBox;

public class LiftInstance {
	
	
	private final World world;
	
	private GroundFloor groundFloor;
	private IntermediateFloor[] intermediateFloors;
	private LiftFloor[] floors;
	
	private BoundingBox boundingBox;
	
	public LiftInstance(GroundFloor groundFloor, IntermediateFloor[] intermediateFloors){
		this.world = groundFloor.getButton().getWorld();
		this.groundFloor = groundFloor;
		this.intermediateFloors = intermediateFloors;
	}
	
	public World getWorld(){
		return world;
	}
	
	public GroundFloor getGroundFloor(){
		return groundFloor;
	}
	
	public IntermediateFloor[] getIntermediateFloors(){
		return intermediateFloors;
	}
	
	public IntermediateFloor getTopFloor(){
		return intermediateFloors[intermediateFloors.length-1];
	}
	
	public BoundingBox getBoundingBox(){
		return boundingBox;
	}
	
	public int getFloorPosition(LiftFloor floor){
		for(int i = 0; i < floors.length; i++){
			if(floors[i]!=floor) continue;
			return i;
		}
		
		return -1;
	}
	
	public LiftFloor getFloor(int floorIndex){
		return floors[floorIndex >= 0 && floorIndex<floors.length ? floorIndex : 0];
	}
	
	public LiftFloor getFloor(Block block){
		for(LiftFloor floor : this.floors){
			if(BlockUtil.compare(floor.getFloorSign(), block)) return floor;
			if(BlockUtil.compare(floor.getButton(), block)) return floor;
			if(BlockUtil.compare(floor.getNameSign(), block)) return floor;
			
		}
		return null;
	}
	
	public LiftType getType(){
		return this.groundFloor.getType();
	}
	
	public int getFloorCount(){
		return floors.length;
	}
	
	private void initialize(){
		LiftInstances.add(this);
		this.updateFloors();
		this.recalculateBoundingBox();
		for(LiftFloor floor : floors){
			floor.initialize(this);
		}
	}
	
	private void updateFloors(){
		this.floors = new LiftFloor[intermediateFloors.length+1];
		this.floors[0] = groundFloor;
		for(int i = 0; i < this.intermediateFloors.length; i++){
			this.floors[i+1] = this.intermediateFloors[i];
		}
	}
	
	protected void release(){
		LiftInstances.remove(this);
	}
	
	public void recalculateBoundingBox(){
		Block min = BlockUtil.getMin(groundFloor.getBlocks());
		Block max = BlockUtil.getMax(groundFloor.getBlocks());
		int height = intermediateFloors[intermediateFloors.length-1].getY() - groundFloor.getY();
		boundingBox = BoundingBox.of(min, max.getRelative(BlockFace.UP, height + 3));
	}
	
	public static LiftInstance get(Block block){
		LiftInstance existing = LiftInstance.findExisting(block);
		if(existing!=null){
			Debug.Log("Existing LiftInstance found");
			return existing;
		}
		GroundFloor groundFloor = GroundFloor.get(block);
		if(groundFloor==null){
			Debug.Log("GroundFloor not found");
			return null;
		}
		IntermediateFloor[] intermediateFloors = LiftInstance.getIntermediateFloors(groundFloor);
		if(intermediateFloors==null || intermediateFloors.length==0){
			Debug.Log("No IntermediateFloors  found");
			return null;
		}
		LiftInstance result = new LiftInstance(groundFloor, intermediateFloors);
		result.initialize();
		return result;
	}
	
	private static LiftInstance findExisting(Block block){
		for(LiftInstance existing : LiftInstances.getAll()){
			if(existing.getBoundingBox().contains(block.getX(), block.getY(), block.getZ())) return existing;
		}
		return null;
	}
	
	private static IntermediateFloor[] getIntermediateFloors(GroundFloor groundFloor){
		List<IntermediateFloor> resultList = new ArrayList<IntermediateFloor>(10);
		int floorIndex = groundFloor.getFloorIndex() + 1;
		int remaining = 256;
		List<Block> currentFloor = groundFloor.getBlocks();
		while(remaining>0){
			remaining--;
			List<Block> nextFloor = BlockUtil.getNextFloor(currentFloor);
			if(nextFloor==null) break;
			IntermediateFloor intermediateFloor = IntermediateFloor.get(floorIndex, nextFloor);
			if(intermediateFloor==null) break;
			currentFloor = intermediateFloor.getBlocks();
			resultList.add(intermediateFloor);
			floorIndex++;
		}
		
		IntermediateFloor[] result = new IntermediateFloor[resultList.size()];
		resultList.toArray(result);
		return result;
	}
}
