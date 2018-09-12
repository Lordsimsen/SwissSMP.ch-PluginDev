package ch.swisssmp.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockUtil {
	public static Block getClosest(Location location, double range, Material...materials){
		List<Block> available = BlockUtil.getNearby(location, range, materials);
		if(available.size()==0) return null;
		Block closest = available.get(0);
		double closestDistance = closest.getLocation().distanceSquared(location);
		Block current;
		double currentDistance;
		for(int i = 1; i < available.size(); i++){
			current = available.get(i);
			currentDistance = current.getLocation().distanceSquared(location);
			if(currentDistance<closestDistance){
				closest = current;
				closestDistance = currentDistance;
			}
		}
		return closest;
	}
	public static List<Block> getNearby(Location location, double range, Material... materials){
		Chunk center = location.getWorld().getChunkAt(location);
		int chunkRange = Mathf.ceilToInt(range/16);
		Collection<Chunk> chunks = ChunkUtil.getChunks(center.getWorld(), center.getX(), center.getZ(), chunkRange);
		Block current;
		double rangeSquared = Math.pow(range, 2);
		List<Material> materialsList = Arrays.asList(materials);
		List<Block> result = new ArrayList<Block>();
		for(Chunk chunk : chunks){
			for(int y = (int)Math.max(0, location.getY()-(range+1)); y < Math.min(location.getWorld().getMaxHeight(), location.getY()+range+1); y++){
				for(int x = 0; x < 16; x++){
					for(int z = 0; z < 16; z++){
						current = chunk.getBlock(x,y,z);
						if(!materialsList.contains(current.getType())) continue;
						if(current.getLocation().distanceSquared(location)>rangeSquared) continue;
						result.add(current);
					}
				}
			}
		}
		return result;
	}
}
