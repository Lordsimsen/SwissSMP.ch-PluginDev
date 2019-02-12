package ch.swisssmp.world.border;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;

import ch.swisssmp.world.WorldManager;

public class WorldBorderManager {

	static HashMap<String,WorldBorder> worldBorders = new HashMap<String,WorldBorder>();
	
	/**
	 * Gets the WorldBorder associated with a World
	 * @param worldName - The name of the World to look for
	 * @return A <code>WorldBorder</code> if found;
	 *         <code>null</code> otherwise
	 */
	public static WorldBorder getWorldBorder(String worldName){
		return WorldBorderManager.worldBorders.get(worldName);
	}
	
	public static void removeWorldBorder(String worldName){
		if(worldBorders.containsKey(worldName)) worldBorders.remove(worldName);
	}
	
	public static void setWorldBorder(String worldName, WorldBorder worldBorder){
		WorldBorderManager.removeWorldBorder(worldName);
		worldBorders.put(worldName, worldBorder);
		World world = Bukkit.getWorld(worldName);
		WorldBorderManager.applyWorldBorder(world, worldBorder);
		WorldManager.saveWorldSettings(world);
	}
	
	public static void startBorderChecker(){
		WorldBorderChecker worldBorder = new WorldBorderChecker();
		worldBorder.runTaskTimer(WorldManager.getInstance(), 0, 100L);
	}
	
	private static void applyWorldBorder(World world, WorldBorder worldBorder){
		//Apply World Border
		if(worldBorder!=null && !worldBorder.doWrap()){
			world.getWorldBorder().setCenter(worldBorder.getCenterX(), worldBorder.getCenterZ());
			world.getWorldBorder().setSize(worldBorder.getRadius()*2);
			world.getWorldBorder().setWarningDistance(worldBorder.getMargin());
		}
		else if(worldBorder==null || (worldBorder!=null && worldBorder.doWrap())){
			world.getWorldBorder().setSize(999999999);
		}
	}
	
	/*
	private static void purgeOutsideChunks(World world, int chunkRadius){
		WorldBorder worldBorder = WorldBorderManager.getWorldBorder(world.getName());
		if(worldBorder==null) return; //world does not have a world border, do not purge chunks
		Chunk center = world.getChunkAt(world.getBlockAt(worldBorder.getCenterX(),0,worldBorder.getCenterZ()));
		
		int extra_margin = 100;
		int min_x = worldBorder.getCenterX() - worldBorder.getRadius() - extra_margin;
		int max_x = worldBorder.getCenterX() + worldBorder.getRadius() + extra_margin;
		int min_z = worldBorder.getCenterZ() - worldBorder.getRadius() - extra_margin;
		int max_z = worldBorder.getCenterZ() + worldBorder.getRadius() + extra_margin;
		
		Chunk minChunk = world.getChunkAt(world.getBlockAt(min_x, 0, min_z));
		Chunk maxChunk = world.getChunkAt(world.getBlockAt(max_x, 0, max_z));
		
		for(int x = -chunkRadius; x <= chunkRadius; x++){
			for(int z = -chunkRadius; z <= chunkRadius; z++){
				int chunk_x = center.getX() + x;
				int chunk_z = center.getZ() + z;
				Chunk chunk = world.getChunkAt(chunk_x, chunk_z);
				if(chunk.getX()>=minChunk.getX() && chunk.getX()<=maxChunk.getX() &&
						chunk.getZ()>=minChunk.getZ() && chunk.getZ()<=maxChunk.getZ()) continue;
				world.chunk
			}
		}
	}
	*/
}
