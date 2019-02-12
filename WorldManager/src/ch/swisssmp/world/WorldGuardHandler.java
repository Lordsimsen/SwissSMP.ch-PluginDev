package ch.swisssmp.world;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import ch.swisssmp.utils.FileUtil;
import ch.swisssmp.world.transfer.WorldPackEvent;
import ch.swisssmp.world.transfer.WorldUnpackEvent;

public class WorldGuardHandler implements Listener{
	
	@EventHandler
	private void onWorldPack(WorldPackEvent event){
		Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if(plugin==null){
			Bukkit.getLogger().info("[WorldManager] WorldGuard missing.");
			return;
		}
		File worldGuardDirectory = new File(plugin.getDataFolder(), "worlds/"+event.getWorldName());
		File packedWorldGuardDirectory = new File(event.getPackedDirectory(), "WorldGuard");
		FileUtil.copyDirectory(worldGuardDirectory, packedWorldGuardDirectory);
	}
	
	@EventHandler
	private void onWorldUnpack(WorldUnpackEvent event){
		Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if(plugin==null){
			Bukkit.getLogger().info("[WorldManager] WorldGuard missing.");
			return;
		}
		File packedWorldGuardDirectory = new File(event.getPackedDirectory(), "WorldGuard");
		File worldGuardDirectory = new File(plugin.getDataFolder(), "worlds/"+event.getWorldName());
		if(!worldGuardDirectory.exists()) worldGuardDirectory.mkdirs();
		if(!packedWorldGuardDirectory.exists()){
			Bukkit.getLogger().info("[WorldManager] Packed WorldGuard directory missing: "+packedWorldGuardDirectory.getPath());
			File file = packedWorldGuardDirectory;
			while(file!=null){
				Bukkit.getLogger().info(file.getPath()+" - "+(file.exists() ? "EXISTS" : "MISSING"));
				file = file.getParentFile();
			}
			return;
		}
		FileUtil.copyDirectory(packedWorldGuardDirectory, worldGuardDirectory);
	}
	
	@EventHandler
	private void onWorldDelete(WorldDeleteEvent event){
		Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if(plugin==null){
			Bukkit.getLogger().info("[WorldManager] WorldGuard missing.");
			return;
		}
		File worldGuardDirectory = new File(plugin.getDataFolder(), "worlds/"+event.getWorldName());
		FileUtil.deleteRecursive(worldGuardDirectory);
	}
}
