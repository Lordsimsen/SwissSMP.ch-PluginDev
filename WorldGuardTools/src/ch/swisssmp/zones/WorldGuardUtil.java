package ch.swisssmp.zones;

import org.bukkit.World;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public class WorldGuardUtil {
	
	public static ProtectedRegion getRegion(World world, String region){
		RegionManager manager = getManager(world);
		if(manager==null) return null;
		return manager.getRegion(region);
	}
	
	public static RegionManager getManager(World world){
		WorldGuard worldGuard = WorldGuard.getInstance();
		if(worldGuard==null) return null;
		WorldGuardPlatform platform = worldGuard.getPlatform();
		RegionContainer container = platform.getRegionContainer();
		return container.get(BukkitAdapter.adapt(world));
	}
}
