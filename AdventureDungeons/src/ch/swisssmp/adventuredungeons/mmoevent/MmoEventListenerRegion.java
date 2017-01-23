package ch.swisssmp.adventuredungeons.mmoevent;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.swisssmp.adventuredungeons.Main;
import ch.swisssmp.adventuredungeons.mmoworld.MmoRegion;
import ch.swisssmp.adventuredungeons.mmoworld.MmoWorld;
import ch.swisssmp.adventuredungeons.mmoworld.MmoWorldInstance;

public class MmoEventListenerRegion extends MmoEventListener{

	public MmoEventListenerRegion(JavaPlugin plugin) {
		super(plugin);
	}
	@EventHandler
	private void onRegionEnterEvent(RegionEnterEvent event){
		World world = event.getPlayer().getWorld();
		MmoWorldInstance worldInstance = MmoWorld.getInstance(world);
		ProtectedRegion protectedRegion = event.getRegion();
		if(worldInstance.regionTriggers==null){
			Main.debug("regionTriggers is null");
			return;
		}
		if(worldInstance.regionTriggers.containsKey(protectedRegion.getId())){
			Player player = event.getPlayer();
			MmoRegion region = worldInstance.regionTriggers.get(protectedRegion.getId());
			region.playerEnter(player, player.getGameMode());
		}
		else{
			Main.debug("regionTriggers does not contain the key "+protectedRegion.getId());
		}
	}
	@EventHandler(ignoreCancelled=true)
	private void onRegionLeaveEvent(RegionLeaveEvent event){
		World world = event.getPlayer().getWorld();
		MmoWorldInstance worldInstance = MmoWorld.getInstance(world);
		ProtectedRegion protectedRegion = event.getRegion();
		if(worldInstance.regionTriggers.containsKey(protectedRegion.getId())){
			Player player = event.getPlayer();
			MmoRegion region = worldInstance.regionTriggers.get(protectedRegion.getId());
			region.playerLeave(player, player.getGameMode());
		}
	}
}
