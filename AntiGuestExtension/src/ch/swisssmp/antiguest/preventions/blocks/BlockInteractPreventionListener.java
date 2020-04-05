package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import ch.swisssmp.antiguest.preventions.Prevention;

public class BlockInteractPreventionListener implements Listener {
	
	private final BlockInteractPrevention[] preventions;
	
	public BlockInteractPreventionListener(BlockInteractPrevention[] preventions) {
		this.preventions = preventions;
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	private void onPlayerInteractBarrel(PlayerInteractEvent event){
		// Bukkit.getLogger().info("InteractBlock?");
		if(event.getAction()!=Action.RIGHT_CLICK_BLOCK ||
				event.useInteractedBlock()!=Result.ALLOW ||
				event.getClickedBlock()==null ||
				event.getPlayer().hasPermission("antiguest_extension.preventions.*")) return;
		// Bukkit.getLogger().info("Let's check...");
		Material material = event.getClickedBlock().getType();
		Player player = event.getPlayer();
		Prevention triggered = null;
		for(BlockInteractPrevention prevention : preventions) {
			// Bukkit.getLogger().info(prevention.GetType()+"?");
			if(material!=prevention.GetType() || player.hasPermission("antiguest_extension.preventions.blocks."+prevention.GetSubPermission())) continue;
			triggered = prevention;
			// Bukkit.getLogger().info("No!");
			break;
		}
		if(triggered==null) return;
		event.setUseInteractedBlock(Result.DENY);
		triggered.trigger((Player) event.getPlayer());
	}
}
