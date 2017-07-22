package ch.swisssmp.smartdismount;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;

public class EventListener implements Listener{

	@EventHandler(ignoreCancelled=true)
	private void onEntityDismount(EntityDismountEvent event){
		Entity entity = event.getEntity();
		if(entity instanceof Player){
			if(((Player)entity).hasPermission("smartdismount.bypass")) return;
		}
		Entity dismounted = event.getDismounted();
		Block target = entity.getLocation().getBlock();
		Block targetFloor = target.getRelative(BlockFace.DOWN);
		if(!target.isLiquid() && !target.getType().isSolid() && !targetFloor.isLiquid() && targetFloor.getType().isSolid()){
			return;
		}
		entity.teleport(dismounted);
	}


}