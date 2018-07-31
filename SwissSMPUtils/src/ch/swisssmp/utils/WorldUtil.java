package ch.swisssmp.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class WorldUtil {
	/**
	 * Loads respective chunks when necessary.
	 * @param world - the world to  check in
	 * @param from - one corner of the bounding box
	 * @param to - opposite corner of bounding box
	 * @return A List of all Etities between from and to (inclusive)
	 */
	public static List<Entity> getEntitiesWithinBoundingBox(World world, Vector from, Vector to) {
	    List<Entity> entities = new ArrayList<Entity>();
	    
	    double min_x = Math.min(from.getX(), to.getX());
	    double max_x = Math.max(from.getX(), to.getX());
	    double min_z = Math.min(from.getZ(), to.getZ());
	    double max_z = Math.max(from.getZ(), to.getZ());
	    Vector min = new Vector(min_x, Math.min(from.getY(), to.getY()),min_z);
	    Vector max = new Vector(max_x, Math.max(from.getY(), to.getY()),max_z);

	    // To find chunks we use chunk coordinates (not block coordinates!)
	    int min_chunk_x = Mathf.floorToInt(min_x / 16.0D);
	    int max_chunk_x = Mathf.floorToInt(max_x / 16.0D);
	    int min_chunk_z = Mathf.floorToInt(min_z / 16.0D);
	    int max_chunk_z = Mathf.floorToInt(max_z / 16.0D);

	    Chunk chunk;
	    for (int x = min_chunk_x; x <= max_chunk_x; x++) {
	        for (int z = min_chunk_z; z <= max_chunk_z; z++) {
	        	chunk = world.getChunkAt(x, z);
	        	if(!chunk.isLoaded()) chunk.load();
                entities.addAll(Arrays.asList(chunk.getEntities())); // Add all entities from this chunk to the list
	        }
	    }
	    
	    // Remove entities that are within the chunks but not actually in the bounding box we defined with the from and to location
	    return entities.stream().filter(e -> VectorUtil.isBetween(e.getLocation().toVector(), min, max)).collect(Collectors.toList());
	}
	/**
	 * Unload a world and delete its data from the disk.
	 * @param world - The world to be deleted
	 * @param leavePoint - Exit point for players still inside the world
	 * @param deleteConfiguration - Delete WorldGuard configuration files as well?
	 * @return True on success, False when something went wrong
	 */
	public static boolean deleteWorld(World world, Location leavePoint, boolean deleteConfiguration){
    	try{
        	String worldName = world.getName();
            if(Bukkit.getServer().unloadWorld(world, true)){
    	    	Bukkit.getLogger().info("[SwissSMPUtils] Welt "+worldName+" geschlossen.");
    			File worldDirectory = new File(Bukkit.getWorldContainer(), worldName);
    	    	WorldUtil.deleteFilesLater(worldDirectory, 20L);
    	    	if(deleteConfiguration) WorldUtil.deleteWorldGuardConfiguration(worldName);
    			WorldGuardPlugin worldGuard = WorldGuardPlugin.inst();
    			if(worldGuard!=null){
        	    	if(deleteConfiguration){
        				File regionConfiguration = new File(worldGuard.getDataFolder(), "worlds/"+worldName);
        				FileUtil.deleteRecursive(regionConfiguration);
        	    	}
        			worldGuard.reloadConfig();
    			}
    	    	return true;
            }
            else{
            	Bukkit.getLogger().info("[SwissSMPUtils] Fehler beim Schliessen der Welt "+worldName);
            	return false;
            }
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
	}
	
	private static void deleteWorldGuardConfiguration(String worldName){
		
	}
	
	private static void deleteFilesLater(File path, long delay){
		Bukkit.getScheduler().runTaskLater(SwissSMPUtils.plugin, new Runnable(){
			public void run(){
				Thread thread = new Thread(()->{
					FileUtil.deleteRecursive(path);
				});
				thread.start();
			}
		}, delay);
	}
}
