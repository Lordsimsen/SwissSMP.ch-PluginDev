package ch.swisssmp.vanillaportals;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

public class PortalLinkCache {
	
	private static List<PortalLinkCache> cache = new ArrayList<PortalLinkCache>();
	
	private final Location a;
	private final Location b;
	
	private BukkitTask task;
	
	private PortalLinkCache(Location a, Location b) {
		this.a = a;
		this.b = b;
	}
	
	public static Location getCached(Location location) {
		World world = location.getWorld();
		for(PortalLinkCache link : cache) {
			Location result;
			if(link.a.getWorld()==world && link.a.distanceSquared(location)<9) {
				result = link.b;
			}
			else if(link.b.getWorld()==world && link.b.distanceSquared(location)<9) {
				result = link.a;
			}
			else {
				continue;
			}
			scheduleCleanup(link);
			return result;
		}
		return null;
	}
	
	protected static PortalLinkCache create(Location a, Location b, long timeToLive) {
		PortalLinkCache result = new PortalLinkCache(a, b);
		cache.add(result);
		scheduleCleanup(result);
		return result;
	}
	
	protected static void scheduleCleanup(PortalLinkCache link) {
		if(link.task!=null) link.task.cancel();
		link.task = Bukkit.getScheduler().runTaskLater(NetherPortalFixer.getInstance(), ()->{
			cache.remove(link);
		}, 60*20); // 60s * 20tps
	}
	
	protected static void clear() {
		cache.clear();
	}
}
