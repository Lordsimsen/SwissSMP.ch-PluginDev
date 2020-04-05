package ch.swisssmp.event.quarantine;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.utils.PlayerRenameItemEvent;

public class EventListener implements Listener {
	@EventHandler
	private void onResourcepackChanged(PlayerResourcePackUpdateEvent event) {
		event.addComponent("quarantine");
	}
	@EventHandler
	private void onWorldLoad(WorldLoadEvent event) {
		ArenaContainers.load(event.getWorld());
	}
	@EventHandler
	private void onWorldUnload(WorldLoadEvent event) {
		ArenaContainers.unload(event.getWorld());
	}
	
	@EventHandler
	private void onItemRename(PlayerRenameItemEvent event) {
		if(event.getItemStack()==null) return;
		Player player = event.getPlayer();
		if(!player.hasPermission("quarantine.admin")) {
			return;
		}
		
		ItemStack itemStack = event.getItemStack();
		Optional<QuarantineArena> arenaQuery = QuarantineArena.get(itemStack);
		if(!arenaQuery.isPresent()) return;
		
		QuarantineArena arena = arenaQuery.get();
		arena.setName(event.getNewName());
		arena.getContainer().save();
	}
	
	/**
	 * Admin Item usage
	 * @param event
	 */
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getItem()==null) return;
		Player player = event.getPlayer();
		if(!player.hasPermission("quarantine.admin")) {
			return;
		}
		
		ItemStack itemStack = event.getItem();
		Optional<QuarantineArena> arena = QuarantineArena.get(itemStack);
		if(!arena.isPresent()) return;
		QuarantineArenaView.open(player, arena.get());
		event.setCancelled(true);
	}
}
