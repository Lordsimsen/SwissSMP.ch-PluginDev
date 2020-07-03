package ch.swisssmp.stairchairs;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.spigotmc.event.entity.EntityDismountEvent;

public class EventListener implements Listener {
	
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event){
		if(event.getHand()!=EquipmentSlot.HAND) {
			StairChairs.debug("Not Mainhand");
			return;
		}
		if(event.getAction()!=Action.RIGHT_CLICK_BLOCK) {
			StairChairs.debug("Not RightClickBlock");
			return;
		}
		Player player = event.getPlayer();
		if((player.getGameMode()!=GameMode.SURVIVAL && player.getGameMode()!=GameMode.CREATIVE) || player.getVehicle()!=null) {
			StairChairs.debug("Wrong GameMode or riding a vehicle");
			return;
		}
		if(event.getItem()!=null) {
			StairChairs.debug("Item in Hand");
			return;
		}
		Block block = event.getClickedBlock();
		if(!StairChairs.sit(player, block).isPresent()) return;
		StairChairs.debug(player.getName()+" sits!");
	}
	
	@EventHandler
	private void onBlockPlace(BlockPlaceEvent event){
		Block block = event.getBlock().getRelative(BlockFace.DOWN);
		if(block==null) return;
		ChairInstances.getInstance(block).ifPresent(ChairInstance::unsit);
	}
	@EventHandler
	private void onBlockChange(BlockBurnEvent event){
		ChairInstances.getInstance(event.getBlock()).ifPresent(ChairInstance::unsit);
	}
	@EventHandler
	private void onBlockBreak(BlockBreakEvent event){
		ChairInstances.getInstance(event.getBlock()).ifPresent(ChairInstance::unsit);
		
	}
	@EventHandler
	private void onEntityDismount(EntityDismountEvent event){
		if(event.getEntity() instanceof Player){
			Player player = (Player) event.getEntity();
			ChairInstances.getInstance(player).ifPresent(ChairInstance::unsit);
		}
	}
	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event){
		Bukkit.getScheduler().runTaskLater(StairChairsPlugin.getInstance(), ()->{
				StairChairs.removeUnusedArmorStands(event.getPlayer().getNearbyEntities(10, 10, 10));
			}, 20L);
	}
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		ChairInstances.getInstance(player).ifPresent(ChairInstance::unsit);
	}
}
