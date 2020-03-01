package ch.swisssmp.zones;

import java.io.File;

import org.bukkit.World;

import ch.swisssmp.world.WorldManager;

public class FileUtil {
	public static File getZonesFile(World world){
		File pluginDirectory = WorldManager.getPluginDirectory(ZonesPlugin.getInstance(), world);
		return new File(pluginDirectory, "zones.yml");
	}
}
