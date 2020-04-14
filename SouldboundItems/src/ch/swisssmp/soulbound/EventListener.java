package ch.swisssmp.soulbound;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CreateCustomItemBuilderEvent;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.SwissSMPler;

public class EventListener implements Listener {
	
	@EventHandler
	private void onCreateCustomItemBuilder(CreateCustomItemBuilderEvent event) {
		ConfigurationSection dataSection = event.getConfigurationSection();
		if(dataSection.contains("soulbound") && dataSection.getBoolean("soulbound")) {
			CustomItemBuilder itemBuilder = event.getCustomItemBuilder();
			itemBuilder.addComponent((ItemStack itemStack)->{
				Soulbinder.setSoulbound(itemStack);
			});
		}
	}
	
	@EventHandler
	private void onPlayerCraft(PrepareItemCraftEvent event) {
		CraftingInventory inventory = event.getInventory();
		if(inventory.getResult()==null) return;

		ItemStack itemStack = inventory.getResult();
		boolean isSoulbound = Soulbinder.isSoulbound(itemStack);
		if(!isSoulbound) {
			return;
		}
		
		Player player = (Player) event.getView().getPlayer();
		if(player.getGameMode()==GameMode.CREATIVE) return;
		Soulbinder.bind(itemStack, player.getUniqueId(), player.getDisplayName(), false);
	}
	
	@EventHandler
	private void onPlayerPickupItem(EntityPickupItemEvent event) {
		ItemStack itemStack = event.getItem().getItemStack();
		if(itemStack==null) return;
		boolean isSoulbound = Soulbinder.isSoulbound(itemStack);
		if(!isSoulbound) {
			return;
		}
		
		UUID owner = Soulbinder.getOwner(itemStack);
		if(owner==null) {
			if(event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				if(player.getGameMode()==GameMode.CREATIVE) return;
				Soulbinder.bind(itemStack, player.getUniqueId(), player.getDisplayName(), false);
			}
			else {
				event.setCancelled(true);
			}
			return;
		}
		
		if(!owner.equals(event.getEntity().getUniqueId()) && !event.getEntity().hasPermission("soulbound.ignore")) {
			if(event.getEntity() instanceof Player) {
				sendSoulbindingBlocksInteraction((Player) event.getEntity());
			}
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	private void onItemDespawn(ItemDespawnEvent event) {
		ItemStack itemStack = event.getEntity().getItemStack();
		if(itemStack==null) return;
		boolean isSoulbound = Soulbinder.isSoulbound(itemStack);
		if(isSoulbound) {
			event.setCancelled(true);
			event.getEntity().setTicksLived(1);
		}
	}
	
	@EventHandler
	private void onItemDamage(EntityDamageEvent event) {
		if(event.getEntityType()!=EntityType.DROPPED_ITEM) return;
		Item item = (Item) event.getEntity();
		ItemStack itemStack = item.getItemStack();
		if(itemStack==null) return;
		boolean isSoulbound = Soulbinder.isSoulbound(itemStack);
		if(isSoulbound) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getEntityType()!=EntityType.ITEM_FRAME) return;
		ItemFrame itemFrame = (ItemFrame) event.getEntity();
		ItemStack itemStack = itemFrame.getItem();
		if(itemStack==null || !Soulbinder.isSoulbound(itemStack)) {
			return;
		}
		UUID owner = Soulbinder.getOwner(itemStack);
		Entity damager = event.getDamager();
		if(owner==null) {
			if(damager instanceof Player) {
				Player player = (Player) damager;
				Soulbinder.bind(itemStack, player.getUniqueId(), player.getDisplayName(), false);
			}
			else {
				event.setCancelled(true);
			}
			return;
		}
		else if(!owner.equals(damager.getUniqueId()) && !damager.hasPermission("soulbound.ignore")) {
			event.setCancelled(true);
			if(damager instanceof Player) {
				sendSoulbindingBlocksInteraction((Player) damager);
			}
			return;
		}
	}
	
	@EventHandler
	private void onInventoryClick(InventoryClickEvent event) {
		ItemStack itemStack = event.getCurrentItem();
		if(itemStack==null || !Soulbinder.isSoulbound(itemStack)) {
			return;
		}
		UUID owner = Soulbinder.getOwner(itemStack);
		Player player = (Player) event.getView().getPlayer();
		if(owner==null) {
			if(player.getGameMode()==GameMode.CREATIVE) return;
			Soulbinder.bind(itemStack, player.getUniqueId(), player.getDisplayName());
		}
		else if(!owner.equals(player.getUniqueId()) && !player.hasPermission("soulbound.ignore")) {
			event.setCancelled(true);
			sendSoulbindingBlocksInteraction(player);
			return;
		}
	}
	
	@EventHandler
	private void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
		ItemStack itemStack = event.getArmorStandItem();
		if(itemStack==null || !Soulbinder.isSoulbound(itemStack)) {
			return;
		}
		UUID owner = Soulbinder.getOwner(itemStack);
		Player player = event.getPlayer();
		if(owner==null) {
			if(player.getGameMode()==GameMode.CREATIVE) return;
			Soulbinder.bind(itemStack, player.getUniqueId(), player.getDisplayName());
		}
		else if(!owner.equals(player.getUniqueId()) && !player.hasPermission("soulbound.ignore")) {
			event.setCancelled(true);
			sendSoulbindingBlocksInteraction(player);
			return;
		}
	}
	
	@EventHandler
	private void onItemFrameManipulate(PlayerInteractEntityEvent event) {
		if(event.getRightClicked().getType()!=EntityType.ITEM_FRAME) return;
		ItemFrame itemFrame = (ItemFrame) event.getRightClicked();
		ItemStack itemStack = itemFrame.getItem();
		if(itemStack==null || !Soulbinder.isSoulbound(itemStack)) return;
		UUID owner = Soulbinder.getOwner(itemStack);
		Player player = event.getPlayer();
		if(owner==null) {
			if(player.getGameMode()==GameMode.CREATIVE) return;
			Soulbinder.bind(itemStack, player.getUniqueId(), player.getDisplayName());
		}
		else if(!owner.equals(player.getUniqueId()) && !player.hasPermission("soulbound.ignore")) {
			event.setCancelled(true);
			sendSoulbindingBlocksInteraction(player);
			return;
		}
	}
	
	@EventHandler
	private void onLecternManipulate(PlayerTakeLecternBookEvent event) {
		ItemStack itemStack = event.getBook();
		if(itemStack==null || !Soulbinder.isSoulbound(itemStack)) {
			return;
		}
		UUID owner = Soulbinder.getOwner(itemStack);
		Player player = event.getPlayer();
		if(owner==null) {
			if(player.getGameMode()==GameMode.CREATIVE) return;
			Soulbinder.bind(itemStack, player.getUniqueId(), player.getDisplayName());
		}
		else if(!owner.equals(player.getUniqueId()) && !player.hasPermission("soulbound.ignore")) {
			event.setCancelled(true);
			sendSoulbindingBlocksInteraction(player);
			return;
		}
	}
	
	@EventHandler
	private void onPlayerDeath(PlayerDeathEvent event) {
		List<ItemStack> soulboundItems = new ArrayList<ItemStack>();
		for(ItemStack itemStack : event.getDrops()) {
			if(itemStack==null) continue;
			boolean isSoulbound = Soulbinder.isSoulbound(itemStack);
			if(!isSoulbound) continue;
			soulboundItems.add(itemStack);
		}
		event.getDrops().removeAll(soulboundItems);
		Bukkit.getScheduler().runTaskLater(SoulboundItemsPlugin.getInstance(), ()->{
			for(ItemStack itemStack : soulboundItems) {
				event.getEntity().getInventory().addItem(itemStack);
			}
		}, 1L);
	}
	
	private void sendSoulbindingBlocksInteraction(Player player) {
		SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Dieses Item ist seelengebunden.");
	}
}
