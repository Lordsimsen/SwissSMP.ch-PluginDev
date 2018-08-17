package ch.swisssmp.chunkupdater;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventListener implements Listener{
	@SuppressWarnings("deprecation")
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event){
		if(event.getItem()==null || event.getItem().getType()!=Material.BOOK) return;
		if(!event.getItem().hasItemMeta() || !event.getItem().getItemMeta().hasDisplayName()) return;
		if(!event.getItem().getItemMeta().getDisplayName().equals("ChunkUpdater")) return;
		if(!event.getPlayer().hasPermission("chunkupdater.admin")) return;
		Block block;
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK) block = event.getClickedBlock();
		else block = event.getPlayer().getLocation().getBlock();
		block.getWorld().regenerateChunk(block.getChunk().getX(), block.getChunk().getZ());
		event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_ANVIL_HIT, 5, 1);
	}
}
