package ch.swisssmp.trophies;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.utils.ItemUtil;

public class EventListener implements Listener {
	
	@EventHandler
	private void onPlayerResourcepackUpdated(PlayerResourcePackUpdateEvent event) {
		event.addComponent("trophies");
	}
	
	@EventHandler
	private void onPrepareItemCraft(PrepareItemCraftEvent event) {
		if(event.getRecipe()==null) return;
		ItemStack result = event.getRecipe().getResult();
		if(result==null) return;
		if(Color.of(result)==null) return;
		if(event.getView().getPlayer().hasPermission("trophypedestals.craft")) return;
		event.getInventory().setResult(null);
	}
	
	@EventHandler(priority=EventPriority.HIGH,ignoreCancelled = true)
	private void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK && event.getItem()!=null && (event.getPlayer().getGameMode()==GameMode.CREATIVE || event.getPlayer().getGameMode()==GameMode.SURVIVAL)) {
			if(onPlayerPlaceTrophyPedestal(event)) return;
		}
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK && !event.getPlayer().isSneaking()) {
			if(onPlayerPlaceTrophy(event)) return;
		}
		if(event.getAction()==Action.LEFT_CLICK_BLOCK && event.getClickedBlock().getType()==Material.BARRIER && (event.getPlayer().getGameMode()==GameMode.CREATIVE || event.getPlayer().getGameMode()==GameMode.SURVIVAL)) {
			if(onPlayerBreakTrophyPedestal(event)) return;
		}
	}
	
	private boolean onPlayerPlaceTrophy(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if(block.getType()!=Material.BARRIER || event.getHand()==EquipmentSlot.OFF_HAND) return false;
		Optional<TrophyPedestal> pedestalQuery = TrophyPedestal.get(block);
		if(!pedestalQuery.isPresent()) return false;
		TrophyPedestal pedestal = pedestalQuery.get();
		playerPlaceTrophy(event.getPlayer(), pedestal);
		event.setCancelled(true);
		return true;
	}
	
	@EventHandler
	private void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if(event.getRightClicked().getType()!=EntityType.DROPPED_ITEM) return;
		Optional<TrophyPedestal> pedestalQuery = TrophyPedestal.get((Item) event.getRightClicked());
		if(!pedestalQuery.isPresent()) return;
		TrophyPedestal pedestal = pedestalQuery.get();
		playerPlaceTrophy(event.getPlayer(), pedestal);
		event.setCancelled(true);
	}
	
	private boolean onPlayerPlaceTrophyPedestal(PlayerInteractEvent event) {
		if(event.getItem().getType()!=Material.ARMOR_STAND) return false;
		if(!event.getPlayer().isSneaking() && event.getClickedBlock().getType().isInteractable()) return false;
		if(!CustomItemController.isTrophyPedestal(event.getItem())) return false;
		
		// at this point it is guaranteed a trophy pedestal, handle placement
		event.setCancelled(true);
		
		Block block = event.getClickedBlock().getRelative(event.getBlockFace());
		if(block.getType()!=Material.AIR) {
			// can only place into air
			return false;
		}
		
		BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(block, block.getState(), event.getClickedBlock(), event.getItem(), event.getPlayer(), true, event.getHand());
		try {
			Bukkit.getPluginManager().callEvent(blockPlaceEvent);
			if(!blockPlaceEvent.canBuild() || blockPlaceEvent.isCancelled()) return false;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		ItemStack itemStack = event.getItem();
		Color color = Color.of(itemStack);
		if(color==null) return false;
		if(event.getPlayer().getGameMode()!=GameMode.CREATIVE) event.getItem().setAmount(event.getItem().getAmount()-1);
		TrophyPedestal.create(block, color);
		
		return true;
	}
	
	private boolean onPlayerBreakTrophyPedestal(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(player.getGameMode()!=GameMode.CREATIVE && player.getGameMode()!=GameMode.SURVIVAL) {
			return false;
		}
		Block block = event.getClickedBlock();
		Optional<TrophyPedestal> pedestalQuery = TrophyPedestal.get(block);
		if(!pedestalQuery.isPresent()) {
			return false;
		}
		TrophyPedestal pedestal = pedestalQuery.get();
		
		BlockBreakEvent blockBreakEvent = new BlockBreakEvent(block,event.getPlayer());
		try {
			Bukkit.getPluginManager().callEvent(blockBreakEvent);
			if(blockBreakEvent.isCancelled()) {
				return false;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}

		event.setCancelled(true);
		
		if(event.getPlayer().getGameMode()==GameMode.CREATIVE) {
			pedestal.remove();
		}
		else {
			pedestal.breakNaturally();
		}
		
		return true;
	}
	
	@EventHandler
	private void onItemDespawn(ItemDespawnEvent event) {
		Optional<UUID> pedestalId = EntityUtility.getTrophyPedestalId(event.getEntity());
		if(!pedestalId.isPresent()) return;
		event.setCancelled(true);
		event.getEntity().setTicksLived(1);
	}
	
	@EventHandler
	private void onEntityDeath(EntityDeathEvent event) {
		if(event.getEntityType()!=EntityType.ARMOR_STAND) return;
		ArmorStand armorStand = (ArmorStand) event.getEntity();
		if(!isTrophyPedestal(armorStand)) return;

		EntityEquipment equipment = armorStand.getEquipment();
		event.getDrops().remove(equipment.getChestplate());
		Optional<ItemStack> armorStandStack = event.getDrops().stream().filter(i->i!=null && i.getType()==Material.ARMOR_STAND).findAny();
		if(armorStandStack.isPresent()) event.getDrops().remove(armorStandStack.get());
		equipment.setChestplate(null);
	}
	
	@EventHandler
	private void onPlayerPlaceBlock(BlockPlaceEvent event) {
		Block below = event.getBlock().getRelative(BlockFace.DOWN);
		if(below.getType()!=Material.BARRIER) return;
		Optional<TrophyPedestal> pedestalQuery = TrophyPedestal.get(below);
		if(!pedestalQuery.isPresent()) return;
		event.setCancelled(true);
	}
	
	@EventHandler
	private void onBlockMove(BlockFromToEvent event) {
		Block below = event.getToBlock().getRelative(BlockFace.DOWN);
		if(below.getType()!=Material.BARRIER) return;
		Optional<TrophyPedestal> pedestalQuery = TrophyPedestal.get(below);
		if(!pedestalQuery.isPresent()) return;
		TrophyPedestal pedestal = pedestalQuery.get();
		if(pedestal.getItem()==null) return;
		ItemStack itemStack = pedestal.getItemStack();
		below.getWorld().dropItem(pedestal.getItem().getLocation(), itemStack);
		pedestal.setItemStack(null);
	}
	
	@EventHandler
	private void onPistonExtend(BlockPistonExtendEvent event) {
		for(Block block : event.getBlocks()) {
			Block below = block.getRelative(event.getDirection()).getRelative(BlockFace.DOWN);
			if(below.getType()!=Material.BARRIER) continue;
			Optional<TrophyPedestal> pedestalQuery = TrophyPedestal.get(below);
			if(!pedestalQuery.isPresent()) continue;
			TrophyPedestal pedestal = pedestalQuery.get();
			if(pedestal.getItem()==null) continue;
			ItemStack itemStack = pedestal.getItemStack();
			below.getWorld().dropItem(pedestal.getItem().getLocation(), itemStack);
			pedestal.setItemStack(null);
		}
	}
	
	@EventHandler
	private void onPistonRectract(BlockPistonRetractEvent event) {
		for(Block block : event.getBlocks()) {
			Block below = block.getRelative(event.getDirection()).getRelative(BlockFace.DOWN);
			if(below.getType()!=Material.BARRIER) continue;
			Optional<TrophyPedestal> pedestalQuery = TrophyPedestal.get(below);
			if(!pedestalQuery.isPresent()) continue;
			TrophyPedestal pedestal = pedestalQuery.get();
			if(pedestal.getItem()==null) continue;
			ItemStack itemStack = pedestal.getItemStack();
			below.getWorld().dropItem(pedestal.getItem().getLocation(), itemStack);
			pedestal.setItemStack(null);
		}
	}
	
	@EventHandler
	private void onEntityChangeBlock(EntityChangeBlockEvent event) {
		if(event.getBlockData().getMaterial()==Material.AIR) return;
		Block block = event.getBlock();
		Block below = block.getRelative(BlockFace.DOWN);
		if(below.getType()!=Material.BARRIER) return;
		Optional<TrophyPedestal> pedestalQuery = TrophyPedestal.get(below);
		if(!pedestalQuery.isPresent()) return;
		TrophyPedestal pedestal = pedestalQuery.get();
		if(pedestal.getItem()==null) return;
		ItemStack itemStack = pedestal.getItemStack();
		below.getWorld().dropItem(pedestal.getItem().getLocation(), itemStack);
		pedestal.setItemStack(null);
	}
	
	@EventHandler
	private void onChunkLoad(ChunkLoadEvent event) {
		EntityUtility.releaseDroppedItems(event.getChunk());
	}
	
	private static void playerPlaceTrophy(Player player, TrophyPedestal pedestal) {
		int slot = player.getInventory().getHeldItemSlot();
		ItemStack itemStack = player.getInventory().getItem(slot);
		ItemStack previous = pedestal.getItemStack();
		if(itemStack==null && previous==null) return;
		if(pedestal.getBlock().getRelative(BlockFace.UP).getType()!=Material.AIR) return;
		PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, pedestal.getArmorStand(), EquipmentSlot.HAND);
		try {
			Bukkit.getPluginManager().callEvent(event);
			if(event.isCancelled()) return;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		pedestal.setItemStack(itemStack);
		if(player.getGameMode()!=GameMode.CREATIVE || player.getInventory().getItem(slot)==null) {
			player.getInventory().setItem(slot, previous);
		}
		player.getWorld().playSound(pedestal.getArmorStand().getLocation(), Sound.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1, 1);
	}
	
	private static boolean isTrophyPedestal(ArmorStand armorStand) {
		if(!armorStand.isSmall()) return false;
		EntityEquipment equipment = armorStand.getEquipment();
		if(equipment.getChestplate()==null) return false;
		String owner = ItemUtil.getString(equipment.getChestplate(), "trophy_pedestal_owner");
		return owner!=null;
	}
}
