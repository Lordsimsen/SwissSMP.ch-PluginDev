package ch.swisssmp.deluminator;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class EventListener implements Listener {
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event){
		if(event.getItem()==null) return;
		if(!event.getPlayer().hasPermission("deluminator.use")) return;
		Deluminator deluminator = Deluminator.get(event.getItem());
		if(deluminator==null) return;
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK){
			if(event.getClickedBlock().getType()==Material.REDSTONE_LAMP_ON){
				deluminator.extinguish(event.getPlayer(), event.getClickedBlock(), event.getBlockFace());
			}
			else{
				//TODO extinguish nearby
			}
		}
		else if(event.getAction()==Action.RIGHT_CLICK_AIR){
			//TODO extinguish nearby
		}
		else if(event.getPlayer().isSneaking() && event.getPlayer().isOp()){
			IgniteTask.run(event.getPlayer());
		}
		else{
			deluminator.igniteAll(event.getPlayer());
		}
	}
	@EventHandler
	private void onWorldUnload(WorldUnloadEvent event){
		Deluminator.resetBlocks(event.getWorld());
	}
	@EventHandler
	private void onBlockPhysics(BlockPhysicsEvent event){
		if(Deluminator.hasDeactivated(event.getBlock())){
			event.setCancelled(true);
		}
	}
}
