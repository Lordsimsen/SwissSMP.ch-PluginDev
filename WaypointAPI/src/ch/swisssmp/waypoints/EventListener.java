package ch.swisssmp.waypoints;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.Position;
import ch.swisssmp.utils.SwissSMPler;

public class EventListener implements Listener {
	
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event){
		if(event.getItem()==null) return;
		if(!event.getPlayer().hasPermission("waypoints.admin")) return;
		Position position = WaypointAPI.getPosition(event.getItem());
		if(position==null) return;
		event.setCancelled(true);
		if(event.getAction()==Action.RIGHT_CLICK_AIR || event.getAction()==Action.RIGHT_CLICK_BLOCK){
			//right click changes waypoint
			onWaypointUse(event); 
		}
		
		else{
			//left click teleports to waypoint
			World attachedWorld = WaypointAPI.getAttachedWorld(event.getItem());
			event.getPlayer().teleport(position.getLocation(attachedWorld!=null ? attachedWorld : event.getPlayer().getWorld()));
		}
	}
	
	private void onWaypointUse(PlayerInteractEvent event){
		Position position = new Position(event.getPlayer().getLocation());
		ItemStack itemStack = event.getItem();
		ItemUtil.setPosition(itemStack, "waypoint", position);
		ItemMeta itemMeta = itemStack.getItemMeta();
		List<String> lore = itemMeta.getLore();
		List<String> templateLore = WaypointAPI.getWaypointLore(position,false);
		lore.set(0, templateLore.get(0));
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.GREEN+"Wegpunkt gesetzt!");
	}
}
