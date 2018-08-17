package ch.swisssmp.world;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import ch.swisssmp.utils.FileUtil;

public class WorldGuardHandler implements Listener{
	
	@EventHandler
	private void onWorldPack(WorldPackEvent event){
		Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if(plugin==null) return;
		File worldGuardDirectory = new File(plugin.getDataFolder(), "worlds/"+event.getWorldName());
		File packedWorldGuardDirectory = new File(event.getPackedDirectory(), "WorldGuard");
		FileUtil.copyDirectory(worldGuardDirectory, packedWorldGuardDirectory);
	}
	
	@EventHandler
	private void onWorldUnpack(WorldUnpackEvent event){
		Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if(plugin==null) return;
		File packedWorldGuardDirectory = new File(event.getPackedDirectory(), "WorldGuard");
		File worldGuardDirectory = new File(plugin.getDataFolder(), "worlds/"+event.getWorldName());
		if(!worldGuardDirectory.exists()) worldGuardDirectory.mkdirs();
		FileUtil.copyDirectory(packedWorldGuardDirectory, worldGuardDirectory);
	}
	
	@EventHandler
	private void onWorldDelete(WorldDeleteEvent event){
		Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if(plugin==null) return;
		File worldGuardDirectory = new File(plugin.getDataFolder(), "worlds/"+event.getWorldName());
		FileUtil.deleteRecursive(worldGuardDirectory);
	}
}
