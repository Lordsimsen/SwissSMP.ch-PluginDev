package ch.swisssmp.citymapdisplays;

import java.util.Arrays;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.LecternInventory;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.mapimageloader.MapViewComposition;

public class EventListener implements Listener {
	
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction()!=Action.RIGHT_CLICK_AIR) return;
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
		if(player.isSneaking()) {
			MapViewComposition[][] mapViews = display.getMapViews();
			World world = player.getWorld();
			Location dropLocation = player.getEyeLocation();
			for(int y = 0; y < mapViews.length; y++) {
				MapViewComposition[] row = mapViews[y];
				for(int x = 0; x < row.length; x++) {
					MapViewComposition composition = row[x];
					if(composition==null) {
						Bukkit.getLogger().info(CityMapDisplaysPlugin.getPrefix()+" Display "+display.getUid()+" is missing a map view composition at "+x+","+y+"!");
						continue;
					}
					ItemStack item = composition.createItemStack();
					ItemMeta itemMeta = item.getItemMeta();
					itemMeta.setLore(Arrays.asList((x+1)+","+(y+1)));
					item.setItemMeta(itemMeta);
					world.dropItem(dropLocation, item);
				}
			}
			event.setCancelled(true);
			return;
		}
		else {
			display.updateItemStack(itemStack);
		}
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
	}
}
