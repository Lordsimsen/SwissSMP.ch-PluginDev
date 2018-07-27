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

import ch.swisssmp.utils.Random;
import ch.swisssmp.utils.SwissSMPler;

public class EventListener implements Listener {
	@EventHandler
	private void onInventoryOpen(InventoryOpenEvent event){
		Inventory inventory = event.getInventory();
		boolean clearInventory = false;
		boolean generateItems = event.getPlayer().getGameMode()==GameMode.ADVENTURE || event.getPlayer().getGameMode()==GameMode.SURVIVAL;
		List<LootTable> lootTables = new ArrayList<LootTable>();
		LootTableQuery lootTableQuery;
		LootTable lootTable;
		for(ItemStack itemStack : inventory){
			if(itemStack==null) continue;
			lootTableQuery = LootTable.get(itemStack);
			if(lootTableQuery.isLootTableToken() && generateItems) clearInventory = true;
			lootTable = lootTableQuery.getLootTable();
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
		LootTableQuery lootTableQuery = LootTable.get(itemStack);
		if(!lootTableQuery.isLootTableToken()) return;
		if(lootTableQuery.getLootTable()!=null){
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
			lootTableQuery.getLootTable().openEditor(event.getPlayer());
		}
		else{
			event.getPlayer().sendMessage("[LootTables] Beutetabelle "+lootTableQuery.getLootTableId()+" nicht gefunden. Vielleicht wurde sie gelöscht? Du kannst mit '/loot info' herausfinden, welche Tabellen existieren.");
		}
	}
}
