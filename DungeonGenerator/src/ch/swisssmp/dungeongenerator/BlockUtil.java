package ch.swisssmp.dungeongenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

//import org.bukkit.Bukkit;
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
	
	public static Collection<Block> getBoundingBox(Block position, int sizeXZ, int sizeY){
		Collection<Block> result = new ArrayList<Block>();
		World world = position.getWorld();
		int template_x = position.getX();
		int template_y = position.getY();
		int template_z = position.getZ();
		for(int x = -1; x < sizeXZ+1; x++){
			result.add(world.getBlockAt(template_x+x, template_y-1, template_z-1));
			result.add(world.getBlockAt(template_x+x, template_y-1, template_z+sizeXZ));
			result.add(world.getBlockAt(template_x+x, template_y+sizeY, template_z-1));
			result.add(world.getBlockAt(template_x+x, template_y+sizeY, template_z+sizeXZ));
		}
		for(int y = -1; y < sizeY+1; y++){
			result.add(world.getBlockAt(template_x-1, template_y+y, template_z-1));
			result.add(world.getBlockAt(template_x-1, template_y+y, template_z+sizeXZ));
			result.add(world.getBlockAt(template_x+sizeXZ, template_y+y, template_z-1));
			result.add(world.getBlockAt(template_x+sizeXZ, template_y+y, template_z+sizeXZ));
		}
		for(int z = -1; z < sizeXZ+1; z++){
			result.add(world.getBlockAt(template_x-1, template_y-1, template_z+z));
			result.add(world.getBlockAt(template_x+sizeXZ, template_y-1, template_z+z));
			result.add(world.getBlockAt(template_x-1, template_y+sizeY, template_z+z));
			result.add(world.getBlockAt(template_x+sizeXZ, template_y+sizeY, template_z+z));
		}
		return result;
	}
	
	public static Collection<Sign> getAttachedSigns(Collection<Block> blocks){
		Collection<Sign> result = new ArrayList<Sign>();
		BlockFace[] faces = new BlockFace[]{BlockFace.NORTH,BlockFace.EAST,BlockFace.SOUTH,BlockFace.WEST};
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
