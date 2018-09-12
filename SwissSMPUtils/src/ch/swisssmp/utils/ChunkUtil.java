package ch.swisssmp.utils;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class ChunkUtil {
	public static Block getChunkMin(Chunk chunk){
		return chunk.getBlock(chunk.getX()*16, 0, chunk.getZ()*16);
	}
	public static Block getChunkMax(Chunk chunk){
		return chunk.getWorld().getHighestBlockAt(chunk.getX()*16+15, chunk.getZ()*16+15);
	}
	
	public static Collection<Block> getAll(Chunk chunk, Material material){
		Collection<Block> result = new ArrayList<Block>();
		World world = chunk.getWorld();
		Block minBlock;
		Block maxBlock;
		Block block;
		minBlock = ChunkUtil.getChunkMin(chunk);
		maxBlock = ChunkUtil.getChunkMax(chunk);
		for(int x = minBlock.getX(); x <= maxBlock.getX(); x++){
			for(int y = minBlock.getY(); y <= maxBlock.getY(); y++){
				for(int z = minBlock.getZ(); z <= maxBlock.getZ(); z++){
					block = world.getBlockAt(x, y, z);
					if(block.getType()!=material) continue;
					result.add(block);
				}
			}
		}
		return result;
	}
	
	public static Collection<Chunk> getChunks(World world, int center_x, int center_z, int range){
		Collection<Chunk> result = new ArrayList<Chunk>();
		Chunk chunk;
		for(int x = -range; x <= range; x++){
			for(int z = -range; z <= range; z++){
				chunk = world.getChunkAt(center_x+x, center_z+z);
				result.add(chunk);
			}
		}
		return result;
	}
}
