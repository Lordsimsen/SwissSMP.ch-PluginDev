package ch.swisssmp.travel;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.npc.event.PlayerInteractNPCEvent;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.PlayerRenameItemEvent;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.YamlConfiguration;

public class EventListener implements Listener {
	
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event){
		if(event.getItem()==null) return;
		if(event.getAction()!=Action.RIGHT_CLICK_AIR && event.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		if(!event.getPlayer().hasPermission("travelsystem.admin")) return;
		TravelStation station = TravelStation.get(event.getItem());
		if(station==null) return;
		station.openEditor(event.getPlayer());
	}
	
	@EventHandler
	private void onPlayerInteractEntity(PlayerInteractEntityEvent event){
		NPCInstance npc = NPCInstance.get(event.getRightClicked());
		if(npc==null) return; //only allow npcs
		
		Player player = event.getPlayer();
		PlayerInventory inventory = player.getInventory();
		
		ItemStack itemStack = (event.getHand()==EquipmentSlot.HAND ? inventory.getItemInMainHand() : inventory.getItemInOffHand());
		if(itemStack==null) return;
		String station_id = ItemUtil.getString(itemStack, "link_travelstation");
		if(station_id==null) return;
		TravelStation station = TravelStation.get(UUID.fromString(station_id));
		if(station==null) return;
		station.setTravelGuide(npc);
		itemStack.setAmount(0);
		SwissSMPler.get(player).sendActionBar(ChatColor.GREEN+"Reiseführer zugewiesen!");
	}
	
	@EventHandler
	private void onPlayerInteractNPC(PlayerInteractNPCEvent event){
		if(event.getHand()!=EquipmentSlot.HAND) return;
		
		YamlConfiguration yamlConfiguration = event.getNPC().getYamlConfiguration();
		if(yamlConfiguration==null || !yamlConfiguration.contains("travelstation")) return;
		String station_id = yamlConfiguration.getString("travelstation");
		TravelStation station = TravelStation.get(UUID.fromString(station_id));
		if(station==null) return;
		if(station.getJourney()!=null && !station.getJourney().getPlayers().contains(event.getPlayer())){
			station.getJourney().join(event.getPlayer());
			return;
		}
		
		station.openDestinationSelection(event.getPlayer());
	}
	
	@EventHandler
	private void onItemRename(PlayerRenameItemEvent event){
		if(!event.getPlayer().hasPermission("travelsystem.admin")) return;
		TravelStation station = TravelStation.get(event.getItemStack());
		if(station==null) return;
		station.setName(event.getNewName());
		event.setName(ChatColor.AQUA+station.getName());
	}
	
	@EventHandler
	private void onWorldLoad(WorldLoadEvent event){
		TravelStations.load(event.getWorld());
	}
	
	@EventHandler
	private void onWorldUnload(WorldUnloadEvent event){
		TravelStations.unload(event.getWorld());
	}
}
