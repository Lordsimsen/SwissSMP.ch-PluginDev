package ch.swisssmp.citymapdisplays;

import java.util.Arrays;
import java.util.Optional;

import ch.swisssmp.custompaintings.CustomPainting;
import ch.swisssmp.custompaintings.CustomPaintings;
import ch.swisssmp.custompaintings.PaintingPlacer;
import ch.swisssmp.utils.PlayerRenameItemEvent;
import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.LecternInventory;

public class EventListener implements Listener {
	
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction()!=Action.RIGHT_CLICK_AIR && event.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		ItemStack itemStack = event.getItem();
		if(itemStack==null || itemStack.getType()!=Material.WRITTEN_BOOK) {
			// Bukkit.getLogger().info(CityMapDisplaysPlugin.getPrefix()+" Not a written book.");
			return;
		}
		if(!event.getPlayer().hasPermission("citymapdisplays.admin")) {
			// Bukkit.getLogger().info(CityMapDisplaysPlugin.getPrefix()+" No permission to use a City Handbook.");
			return;
		}
		Optional<CityMapDisplay> displayQuery = CityMapDisplay.get(itemStack);
		if(!displayQuery.isPresent()) {
			// Bukkit.getLogger().info(CityMapDisplaysPlugin.getPrefix()+" Not a City Handbook.");
			return;
		}
		CityMapDisplay display = displayQuery.get();
		Player player = event.getPlayer();
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK
				&& player.hasPermission("citymapdisplays.admin")
				&& player.getGameMode()==GameMode.CREATIVE
				&& event.getClickedBlock().getType()!=Material.LECTERN){
			CustomPainting painting = display.getPainting();
			if(painting==null){
				SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Etwas ist schiefgelaufen.");
				return;
			}
			event.setCancelled(true);
			boolean success = CustomPaintings.place(painting, event);
			if(!success){
				SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.RED+"Nicht genügend Platz.");
			}
			return;
		}
		display.updateItemStack(itemStack);
	}
	
	@EventHandler
	private void onPlayerOpenLectern(InventoryOpenEvent event) {
		if(event.getView().getType()!=InventoryType.LECTERN) return;
		LecternInventory lecternInventory = (LecternInventory) event.getInventory();
		ItemStack itemStack = lecternInventory.getItem(0);
		if(itemStack==null || itemStack.getType()!=Material.WRITTEN_BOOK) return;
		Optional<CityMapDisplay> displayQuery = CityMapDisplay.get(itemStack);
		if(!displayQuery.isPresent()) return;
		CityMapDisplay display = displayQuery.get();
		display.updateItemStack(itemStack);
		lecternInventory.setItem(0, itemStack);
	}

	@EventHandler
	private void onItemRename(PlayerRenameItemEvent event){
		if(!event.getPlayer().hasPermission("citymapdisplays.admin")) return;
		CityMapDisplay display = CityMapDisplay.get(event.getItemStack()).orElse(null);
		if(display==null) return;
		display.setName(event.getNewName());
		event.setName(ChatColor.RESET+display.getName());
		CityMapDisplays.save();
	}
}
