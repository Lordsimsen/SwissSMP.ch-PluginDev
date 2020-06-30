package ch.swisssmp.lift;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class BlockUtil {
	
	private static BlockFace[] searchDirections = new BlockFace[]{
			BlockFace.NORTH, 
			BlockFace.NORTH_EAST, 
			BlockFace.EAST, 
			BlockFace.SOUTH_EAST, 
			BlockFace.SOUTH, 
			BlockFace.SOUTH_WEST, 
			BlockFace.WEST, 
			BlockFace.NORTH_WEST
	};
	
	public static Block getMin(List<Block> floor){
		Block first = floor.get(0);
		World world = first.getWorld();
		int x = first.getX();
		int y = first.getY();
		int z = first.getZ();
		for(Block block : floor){
			x = Math.min(block.getX(), x);
			y = Math.min(block.getY(), y);
			z = Math.min(block.getZ(), z);
		}
		return world.getBlockAt(x, y, z);
	}
	
	public static Block getMax(List<Block> floor){
		Block first = floor.get(0);
		World world = first.getWorld();
		int x = first.getX();
		int y = first.getY();
		int z = first.getZ();
		for(Block block : floor){
			x = Math.max(block.getX(), x);
			y = Math.max(block.getY(), y);
			z = Math.max(block.getZ(), z);
		}
		return world.getBlockAt(x, y, z);
	}
	
	/**
	 * Gets the corresponding floor
	 * @param block - Any block in the shaft
	 * @return The floor block below
	 */
	public static Block getFloorBlock(Block block){
		Block current = block;
		int remaining = 256;
		while(remaining>0){
			remaining--;
			if(MaterialUtil.isIntermediateFloor(current.getType())) return current;
			if(MaterialUtil.isGroundFloor(current.getType())) return current;
			if(!MaterialUtil.isAllowedInShaft(current.getType())){
				Debug.Log("Invalid block found in shaft");
				return null;
			}
			if(current.getY()<=0){
				Debug.Log("No Floor block found");
				return null;
			}
			current = current.getRelative(BlockFace.DOWN);
		}
		Debug.Log("No Floor block found");
		return null;
	}
	
	/**
	 * Gets the ground floor
	 * @param block - Any block in the shaft
	 * @return The ground floor block below the given block
	 */
	public static Block getGroundFloorBlock(Block block){
		Block current = block;
		int remaining = 256;
		while(remaining>0){
			remaining--;
			if(MaterialUtil.isGroundFloor(current.getType())) return current;
			if(!MaterialUtil.isAllowedInShaft(current.getType())){
				Debug.Log("Invalid block found in shaft: "+current.getType());
				return null;
			}
			if(current.getY()<=0){
				Debug.Log("No GroundFloor block found");
				return null;
			}
			current = current.getRelative(BlockFace.DOWN);
		}
		Debug.Log("No GroundFloor block found");
		return null;
	}
	
	/**
	 * Checks how many blocks up the next floor is
	 * @param block - A block within the current floor
	 * @return The exact height to the next floor or -1 if none was found
	 */
	private static int getFloorHeight(Block block){
		Block current = block.getRelative(BlockFace.UP,4);
		World world = current.getWorld();
		int height = 4;
		int remaining = 256;
		while(remaining>0){
			remaining--;
			if(MaterialUtil.isIntermediateFloor(current.getType())) return height;
			if(current.getY()>=world.getMaxHeight()) return -1;
			current = current.getRelative(BlockFace.UP);
			height++;
		}
		return -1;
	}
	
	/**
	 * Gets the next upper floor
	 * @param floor - All the blocks in the current floor
	 * @return All blocks in the next floor or null if none was found
	 */
	public static List<Block> getNextFloor(List<Block> floor){
		if(floor.size()==0){
			Debug.Log("Floor is empty");
			return null;
		}
		Block sample = floor.get(0);
		int height = BlockUtil.getFloorHeight(sample);
		if(height<0){
			Debug.Log("No next floor found");
			return null;
		}
		List<Block> result = new ArrayList<Block>();
		for(Block block : floor){
			Block relative = block.getRelative(BlockFace.UP, height);
			if(!MaterialUtil.isIntermediateFloor(relative.getType())){
				Debug.Log("Next floor is incomplete");
				return null;
			}
			result.add(relative);
		}
		Debug.Log("Next floor detected");
		return result;
	}
	
	/**
	 * Gets the nearest floor below the given block
	 * @param block - A block within the shaft, it may or may not be part of the floor
	 * @return All blocks belonging to the floor
	 */
	public static List<Block> getFloor(Block block){
		Block floorBlock = BlockUtil.getFloorBlock(block);
		if(floorBlock==null){
			Debug.Log("Floor block not found");
			return null;
		}
		List<Block> result = new ArrayList<Block>();
		Stack<Block> queue = new Stack<Block>();
		queue.push(floorBlock);
		int remaining = 64;
		while(queue.size()>0 && remaining > 0){
			Block current = queue.remove(0);
			if(current.getType()!=floorBlock.getType()) continue;
			remaining--;
			for(BlockFace direction : searchDirections){
				Block relative = current.getRelative(direction);
				if(result.contains(relative) || queue.contains(relative)) continue;
				queue.add(relative);
			}
			result.add(current);
		}
		return result;
	}
	
	/**
	 * Gets the nearest intermediate floor below the given block
	 * @param block - A block within the shaft, it may or may not be part of the intermediate floor
	 * @return All blocks belonging to the intermediate floor
	 */
	public static List<Block> getIntermediateFloor(Block block){
		Block floorBlock = BlockUtil.getFloorBlock(block);
		if(floorBlock==null || !MaterialUtil.isIntermediateFloor(floorBlock.getType())){
			Debug.Log("IntermediateFloor block not found");
			return null;
		}
		List<Block> result = new ArrayList<Block>();
		Stack<Block> queue = new Stack<Block>();
		queue.push(floorBlock);
		int remaining = 64;
		while(queue.size()>0 && remaining > 0){
			Block current = queue.remove(0);
			if(!MaterialUtil.isIntermediateFloor(current.getType())) continue;
			remaining--;
			for(BlockFace direction : searchDirections){
				Block relative = current.getRelative(direction);
				if(result.contains(relative) || queue.contains(relative)) continue;
				queue.add(relative);
			}
			result.add(current);
		}
		Debug.Log("IntermediateFloor contains "+result.size()+" blocks");
		return result;
	}

	/**
	 * Gets the ground floor below the given block
	 * @param block - A block within the shaft, it may or may not be part of the ground floor
	 * @return All blocks belonging to the ground floor
	 */
	public static List<Block> getGroundFloor(Block block){
		Block floorBlock = BlockUtil.getGroundFloorBlock(block);
		if(floorBlock==null || !MaterialUtil.isGroundFloor(floorBlock.getType())){
			Debug.Log("GroundFloor block not found");
			return null;
		}
		List<Block> result = new ArrayList<Block>();
		Stack<Block> queue = new Stack<Block>();
		queue.push(floorBlock);
		int remaining = 64;
		while(queue.size()>0 && remaining > 0){
			Block current = queue.remove(0);
			if(current.getType()!=floorBlock.getType()) continue;
			remaining--;
			for(BlockFace direction : searchDirections){
				Block relative = current.getRelative(direction);
				if(result.contains(relative) || queue.contains(relative)) continue;
				queue.add(relative);
			}
			result.add(current);
		}
		Debug.Log("GroundFloor contains "+result.size()+" blocks");
		return result;
	}
	
	/**
	 * Gets the lift button for the given floor
	 * @param floor - All the blocks of the floor
	 * @return The button corresponding to this floor
	 */
	public static Block getButtonBlock(List<Block> floor){
		Block button = null;
		for(Block block : floor){
			Block current = block.getRelative(BlockFace.UP, 2);
			if(!MaterialUtil.isButton(current.getType())) continue;
			button = current;
			break;
		}
		
		return button;
	}
	
	public static boolean compare(Block a, Block b){
		Debug.Log("Compare "+a.getWorld().getName()+" ("+a.getX()+", "+a.getY()+", "+a.getZ()+") with "+b.getWorld().getName()+" ("+b.getX()+", "+b.getY()+", "+b.getZ()+")");
		return a.getWorld().getName()==b.getWorld().getName() && a.getX()==b.getX() && a.getY()==b.getY() && a.getZ()==b.getZ();
	}
}
