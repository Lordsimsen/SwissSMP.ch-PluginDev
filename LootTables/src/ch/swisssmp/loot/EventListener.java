package ch.swisssmp.loot;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.PlayerRenameItemEvent;
import ch.swisssmp.utils.Random;
import ch.swisssmp.utils.SwissSMPler;

public class EventListener implements Listener {
	@EventHandler
	private void onInventoryOpen(InventoryOpenEvent event){
		Inventory inventory = event.getInventory();
		boolean clearInventory = false;
		boolean generateItems = event.getPlayer().getGameMode()==GameMode.ADVENTURE || event.getPlayer().getGameMode()==GameMode.SURVIVAL;
		List<LootTable> lootTables = new ArrayList<LootTable>();
		int loot_table_id;
		LootTable lootTable;
		for(ItemStack itemStack : inventory){
			if(itemStack==null) continue;
			loot_table_id = ItemUtil.getInt(itemStack, "loot_table");
			if(loot_table_id==0) continue;
			if(generateItems) clearInventory = true;
			lootTable = LootTable.get(loot_table_id);
			if(lootTable==null) continue;
			if(generateItems){
				lootTables.add(lootTable);
			}
			else{
				lootTable.updateToken(itemStack);
			}
		}
		if(clearInventory) inventory.clear();
		if(generateItems && lootTables.size()>0){
			Random random = new Random();
			random.setSeed(LootTable.makeStaticSeed(inventory));
			double totalWeight = 0;
			double chance;
			double defaultChance = 1/(float)lootTables.size();
			for(LootTable candidate : lootTables){
				chance = candidate.getChance()>0?candidate.getChance():defaultChance;
				totalWeight+=chance;
			}
			totalWeight = Math.max(totalWeight,1);
			double randomValue = random.nextDouble()*totalWeight;
			for(LootTable candidate : lootTables){
				chance = candidate.getChance()>0?candidate.getChance():defaultChance;
				if(chance<randomValue) randomValue-=chance;
				else{
					candidate.populate(inventory);
					return;
				}
			}
		}
	}
	
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event){
		if(event.getPlayer().getGameMode()!=GameMode.CREATIVE) return;
		if(event.getAction()!=Action.RIGHT_CLICK_AIR && event.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		if(event.getItem()==null) return;
		ItemStack itemStack = event.getItem();
		int loot_table_id = ItemUtil.getInt(itemStack, "loot_table");
		LootTable lootTable = LootTable.get(loot_table_id);
		if(lootTable!=null){
			if(event.getAction()==Action.RIGHT_CLICK_BLOCK){
				Block block = event.getClickedBlock();
				if(block.getState() instanceof InventoryHolder){
					event.setCancelled(true);
					Inventory inventory = ((InventoryHolder)block.getState()).getInventory();
					boolean found = false;
					for(ItemStack inventoryStack : inventory){
						if(inventoryStack==null || !inventoryStack.isSimilar(itemStack)) continue;
						found = true;
						break;
					}
					if(found){
						inventory.remove(itemStack);
						SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.GRAY+"Beutetabelle entfernt.");
					}
					else{
						ItemStack newToken = itemStack.clone();
						newToken.setAmount(1);
						inventory.addItem(newToken);
						SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.GREEN+"Beutetabelle hinzugefügt.");
					}
					return;
				}
			}
			lootTable.openEditor(event.getPlayer());
		}
		else if(loot_table_id>0){
			itemStack.setAmount(0);
			event.getPlayer().sendMessage("[LootTables] Beutetabelle nicht gefunden. Vielleicht wurde sie gelöscht? Du kannst mit '/loot' herausfinden, welche Tabellen existieren.");
		}
	}
	
	@EventHandler
	private void onItemRename(PlayerRenameItemEvent event){
		int loot_table_id = ItemUtil.getInt(event.getItemStack(), "loot_table");
		if(loot_table_id==0) return;
		LootTable lootTable = LootTable.get(loot_table_id);
		if(lootTable==null){
			event.getItemStack().setAmount(0);
			event.setCancelled(true);
			return;
		}
		lootTable.setName(event.getNewName());
		event.setName(lootTable.getDisplayName());
		lootTable.updateTokens();
	}
	
	@EventHandler
	private void onResourcepackReload(PlayerResourcePackUpdateEvent event){
		if(!event.getPlayer().hasPermission("loottables.admin")) return;
		event.addComponent("loot_editor");
	}
}
