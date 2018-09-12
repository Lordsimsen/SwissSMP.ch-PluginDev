package ch.swisssmp.dungeongenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.material.Colorable;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wood;
import org.bukkit.util.BlockVector;

public class BlockUtil {
	private static Collection<Material> ignoreTypes = Arrays.asList(Material.AIR,Material.SIGN_POST,Material.WALL_SIGN,Material.BARRIER);
	
	public static String getSignature(World world, BlockVector from, BlockVector to){
		String result = "";
		BlockVector min = new BlockVector(Math.min(from.getBlockX(), to.getBlockX()), Math.min(from.getBlockY(), to.getBlockY()), Math.min(from.getBlockZ(), to.getBlockZ()));
		BlockVector max = new BlockVector(Math.max(from.getBlockX(), to.getBlockX()), Math.max(from.getBlockY(), to.getBlockY()), Math.max(from.getBlockZ(), to.getBlockZ()));
		int index = 0;
		for(int y = min.getBlockY(); y <= max.getBlockY(); y++){
			for(int x = min.getBlockX(); x <= max.getBlockX(); x++){
				for(int z = min.getBlockZ(); z <= max.getBlockZ(); z++){
					result+=getBlockIdentifier(world.getBlockAt(x, y, z), index);
					index++;
				}
			}
		}
		//Bukkit.getLogger().info("[DungeonGenerator] Signature for Area ("+min.getBlockX()+","+min.getBlockY()+","+min.getBlockZ()+") to ("+max.getBlockX()+","+max.getBlockY()+","+max.getBlockZ()+"): "+result);
		return result.trim();
	}
	public static String getSignature(World world, BlockVector from, BlockVector to, int rotationSteps){
		rotationSteps = rotationSteps % 4;
		switch(rotationSteps){
		case 1: return getSignature90degrees(world, from, to);
		case 2: return getSignature180degrees(world, from, to);
		case 3: return getSignature270degrees(world, from, to);
		default: return getSignature(world,from,to);
		}
	}
	private static String getSignature90degrees(World world, BlockVector from, BlockVector to){
		String result = "";
		BlockVector min = new BlockVector(Math.min(from.getBlockX(), to.getBlockX()), Math.min(from.getBlockY(), to.getBlockY()), Math.min(from.getBlockZ(), to.getBlockZ()));
		BlockVector max = new BlockVector(Math.max(from.getBlockX(), to.getBlockX()), Math.max(from.getBlockY(), to.getBlockY()), Math.max(from.getBlockZ(), to.getBlockZ()));
		int index = 0;
		for(int y = min.getBlockY(); y <= max.getBlockY(); y++){
			for(int z = max.getBlockZ(); z >= min.getBlockZ(); z--){
				for(int x = min.getBlockX(); x <= max.getBlockX(); x++){
					result+=getBlockIdentifier(world.getBlockAt(x, y, z), index);
					index++;
				}
			}
		}
		//Bukkit.getLogger().info("[DungeonGenerator] Signature for Area ("+min.getBlockX()+","+min.getBlockY()+","+min.getBlockZ()+") to ("+max.getBlockX()+","+max.getBlockY()+","+max.getBlockZ()+"): "+result);
		return result.trim();
		
	}
	private static String getSignature180degrees(World world, BlockVector from, BlockVector to){
		String result = "";
		BlockVector min = new BlockVector(Math.min(from.getBlockX(), to.getBlockX()), Math.min(from.getBlockY(), to.getBlockY()), Math.min(from.getBlockZ(), to.getBlockZ()));
		BlockVector max = new BlockVector(Math.max(from.getBlockX(), to.getBlockX()), Math.max(from.getBlockY(), to.getBlockY()), Math.max(from.getBlockZ(), to.getBlockZ()));
		int index = 0;
		for(int y = min.getBlockY(); y <= max.getBlockY(); y++){
			for(int x = max.getBlockX(); x >= min.getBlockX(); x--){
				for(int z = max.getBlockZ(); z >= min.getBlockZ(); z--){
					result+=getBlockIdentifier(world.getBlockAt(x, y, z), index);
					index++;
				}
			}
		}
		//Bukkit.getLogger().info("[DungeonGenerator] Signature for Area ("+min.getBlockX()+","+min.getBlockY()+","+min.getBlockZ()+") to ("+max.getBlockX()+","+max.getBlockY()+","+max.getBlockZ()+"): "+result);
		return result.trim();
	}
	private static String getSignature270degrees(World world, BlockVector from, BlockVector to){
		String result = "";
		BlockVector min = new BlockVector(Math.min(from.getBlockX(), to.getBlockX()), Math.min(from.getBlockY(), to.getBlockY()), Math.min(from.getBlockZ(), to.getBlockZ()));
		BlockVector max = new BlockVector(Math.max(from.getBlockX(), to.getBlockX()), Math.max(from.getBlockY(), to.getBlockY()), Math.max(from.getBlockZ(), to.getBlockZ()));
		int index = 0;
		for(int y = min.getBlockY(); y <= max.getBlockY(); y++){
			for(int z = min.getBlockZ(); z <= max.getBlockZ(); z++){
				for(int x = max.getBlockX(); x >= min.getBlockX(); x--){
					result+=getBlockIdentifier(world.getBlockAt(x, y, z), index);
					index++;
				}
			}
		}
		//Bukkit.getLogger().info("[DungeonGenerator] Signature for Area ("+min.getBlockX()+","+min.getBlockY()+","+min.getBlockZ()+") to ("+max.getBlockX()+","+max.getBlockY()+","+max.getBlockZ()+"): "+result);
		return result.trim();
	}
	public static String getBlockIdentifier(Block block, int index){
		if(ignoreTypes.contains(block.getType())) return "";
		MaterialData materialData = block.getState().getData();
		if(materialData instanceof Colorable){
			return index+"-"+block.getType()+"-"+((Colorable)materialData).getColor()+".";
		}
		else if(materialData instanceof Wood){
			return index+"-"+block.getType()+"-"+((Wood)materialData).getSpecies()+".";
		}
		else{
			return index+"-"+block.getType()+".";
		}
	}
	
	public static Collection<Block> getBox(Block position){
		Block boxOrigin = BlockUtil.getBoxOrigin(position);
		Block southEnd = BlockUtil.getFurthestValidBlock(boxOrigin, BlockFace.SOUTH, boxOrigin.getType(), 0);
		Block upEnd = BlockUtil.getFurthestValidBlock(boxOrigin, BlockFace.UP, boxOrigin.getType(), 0);
		int sizeXZ = Math.abs(boxOrigin.getZ()-southEnd.getZ())+1;
		int sizeY = Math.abs(boxOrigin.getY()-upEnd.getY())+1;
		return BlockUtil.getBox(boxOrigin, sizeXZ, sizeY);
	}
	
	public static Collection<Block> getBox(Block position, int sizeXZ, int sizeY){
		return BlockUtil.getBox(position, sizeXZ, sizeY, 0);
	}

	public static Collection<Block> getBox(Block position, int sizeXZ, int sizeY, int offset){
		Collection<Block> result = new ArrayList<Block>();
		World world = position.getWorld();
		int template_x = position.getX();
		int template_y = position.getY();
		int template_z = position.getZ();
		for(int x = 0-offset; x < sizeXZ+offset; x++){
			result.add(world.getBlockAt(template_x+x, template_y-offset, template_z-offset));
			result.add(world.getBlockAt(template_x+x, template_y-offset, template_z+(sizeXZ+offset-1)));
			result.add(world.getBlockAt(template_x+x, template_y+(sizeY+offset-1), template_z-offset));
			result.add(world.getBlockAt(template_x+x, template_y+(sizeY+offset-1), template_z+(sizeXZ+offset-1)));
		}
		for(int y = 0-offset; y < sizeY+offset; y++){
			result.add(world.getBlockAt(template_x-offset, template_y+y, template_z-offset));
			result.add(world.getBlockAt(template_x-offset, template_y+y, template_z+(sizeXZ+offset-1)));
			result.add(world.getBlockAt(template_x+(sizeXZ+offset-1), template_y+y, template_z-offset));
			result.add(world.getBlockAt(template_x+(sizeXZ+offset-1), template_y+y, template_z+(sizeXZ+offset-1)));
		}
		for(int z = 0-offset; z < sizeXZ+offset; z++){
			result.add(world.getBlockAt(template_x-offset, template_y-offset, template_z+z));
			result.add(world.getBlockAt(template_x+(sizeXZ+offset-1), template_y-offset, template_z+z));
			result.add(world.getBlockAt(template_x-offset, template_y+(sizeY+offset-1), template_z+z));
			result.add(world.getBlockAt(template_x+(sizeXZ+offset-1), template_y+(sizeY+offset-1), template_z+z));
		}
		return result;
	}

	/**
	 * Looks for the block with the lowest XYZ coordinates still belonging to the box
	 * @param block - A block somewhere in the bounding box
	 * @param boxMaterial - The Material of the bounding box
	 * @return The origin block of the box
	 */
	protected static Block getBoxOrigin(Block block){
		if(block.getRelative(BlockFace.DOWN).getType()==block.getType()){
			block = BlockUtil.getFurthestValidBlock(block, BlockFace.DOWN, block.getType(), 0);
			block = BlockUtil.getFurthestValidBlock(block, BlockFace.NORTH, block.getType(), 0);
			block = BlockUtil.getFurthestValidBlock(block, BlockFace.WEST, block.getType(), 0);
		}
		else if(block.getRelative(BlockFace.NORTH).getType()==block.getType()){
			block = BlockUtil.getFurthestValidBlock(block, BlockFace.NORTH, block.getType(), 0);
			block = BlockUtil.getFurthestValidBlock(block, BlockFace.WEST, block.getType(), 0);
			block = BlockUtil.getFurthestValidBlock(block, BlockFace.DOWN, block.getType(), 0);
		}
		else if(block.getRelative(BlockFace.WEST).getType()==block.getType()){
			block = BlockUtil.getFurthestValidBlock(block, BlockFace.WEST, block.getType(), 0);
			block = BlockUtil.getFurthestValidBlock(block, BlockFace.NORTH, block.getType(), 0);
			block = BlockUtil.getFurthestValidBlock(block, BlockFace.DOWN, block.getType(), 0);
		}
		return block;
	}
	
	protected static Block getFurthestValidBlock(Block block, BlockFace direction, Material material, int tolerance){
		Block lastValidBlock = block;
		int currentTolerance = tolerance;
		int infiniteLoopProtection = 10000;
		while(block.getType()==material || currentTolerance>0){
			block = block.getRelative(direction);
			if(block.getType()==material){
				lastValidBlock = block;
				currentTolerance = tolerance;
			}
			else{
				currentTolerance--;
			}
			infiniteLoopProtection--;
			if(infiniteLoopProtection<0){
				throw new StackOverflowError("Could not find the furst Block with Type "+material.name());
			}
		}
		return lastValidBlock;
	}
	
	public static Collection<Sign> getAttachedSigns(Collection<Block> blocks){
		Collection<Sign> result = new ArrayList<Sign>();
		BlockFace[] faces = new BlockFace[]{BlockFace.NORTH,BlockFace.EAST,BlockFace.SOUTH,BlockFace.WEST,BlockFace.UP};
		Block neighbour;
		for(Block block : blocks){
			for(BlockFace face : faces){
				neighbour = block.getRelative(face);
				if(!(neighbour.getState() instanceof Sign)) continue;
				result.add((Sign)neighbour.getState());
			}
		}
		return result;
	}
}
