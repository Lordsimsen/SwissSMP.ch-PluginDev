package ch.swisssmp.adventuredungeons.world;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.util.AdventureFileUtil;

public class AdventureWorldUtil {
	public static boolean deleteWorld(World world, Location leavePoint, boolean deleteConfiguration){
    	try{
        	String worldName = world.getName();
            if(Bukkit.getServer().unloadWorld(world, true)){
    	    	AdventureDungeons.info("Unloaded world "+worldName);
    			File worldDirectory = new File(Bukkit.getWorldContainer(), worldName);
    	    	AdventureWorldUtil.deleteFilesLater(worldDirectory, 20L);
    			WorldGuardPlugin worldGuard = AdventureDungeons.worldGuardPlugin;
    	    	if(deleteConfiguration){
    				File regionConfiguration = new File(worldGuard.getDataFolder(), "worlds/"+worldName);
    				AdventureFileUtil.deleteRecursive(regionConfiguration);
    	    	}
    			worldGuard.reloadConfig();
    	    	return true;
            }
            else{
            	AdventureDungeons.info("There was an error unloading the world "+worldName);
            	return false;
            }
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
	}
	
	private static void deleteFilesLater(File path, long delay){
		Bukkit.getScheduler().runTaskLater(AdventureDungeons.plugin, new Runnable(){
			public void run(){
				Thread thread = new Thread(()->{
					AdventureFileUtil.deleteRecursive(path);
				});
				thread.start();
			}
		}, delay);
	}
}
