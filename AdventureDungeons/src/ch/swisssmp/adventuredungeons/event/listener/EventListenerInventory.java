package ch.swisssmp.adventuredungeons.event.listener;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Dropper;
import org.bukkit.block.Furnace;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.world.LootInventory;

public class EventListenerInventory extends EventListenerBasic{
	public EventListenerInventory(EventListenerMaster master) {
		super(master);
	}
	@EventHandler
	private void onInventoryOpen(InventoryOpenEvent event){
		HumanEntity player = event.getPlayer();
		if(player.getGameMode()!=GameMode.ADVENTURE) return;
		if(!this.getInstance().getPlayers().contains(player.getUniqueId().toString())) return;
		Inventory inventory = event.getInventory();
		if(inventory instanceof PlayerInventory)
			return;
		InventoryHolder holder = inventory.getHolder();
		if(holder instanceof Chest || holder instanceof DoubleChest || holder instanceof Furnace || holder instanceof Dispenser || holder instanceof Dropper || holder instanceof BrewingStand){
			String action;
			Location location;
			if(holder instanceof Chest){
				AdventureDungeons.info("opening Chest");
				Chest chest = (Chest) holder;
				location = chest.getLocation();
				action = "onchestopen";
			}
			else if(holder instanceof DoubleChest){
				AdventureDungeons.info("opening DoubleChest");
				DoubleChest chest = (DoubleChest) holder;
				location = chest.getLocation();
				action = "onchestopen";
			}
			/*else if(holder instanceof Furnace){
				AdventureDungeons.info("opening Furnace");
				Furnace furnace = (Furnace) holder;
				location = furnace.getLocation();
				action = "onfurnaceopen";
			}
			else if(holder instanceof Dispenser){
				AdventureDungeons.info("opening Dispenser");
				Dispenser dispenser = (Dispenser) holder;
				location = dispenser.getLocation();
				action = "ondispenseropen";
			}
			else if(holder instanceof Dropper){
				AdventureDungeons.info("opening Dropper");
				Dropper dropper = (Dropper) holder;
				location = dropper.getLocation();
				action = "ondropperopen";
			}
			else if(holder instanceof BrewingStand){
				AdventureDungeons.info("opening BrewingStand");
				BrewingStand brewingStand = (BrewingStand) holder;
				location = brewingStand.getLocation();
				action = "onbrewingopen";
			}*/
			else return;
			if(LootInventory.open((Player)event.getPlayer(), action, location.getBlock())){
				event.setCancelled(true);
			}
		}
	}
	@EventHandler
	private void onInventoryClose(InventoryCloseEvent event){
		Inventory inventory = event.getInventory();
		LootInventory lootInventory = this.getInstance().getLootInventory(inventory);
		if(lootInventory!=null){
			boolean empty = true;
			for(ItemStack itemStack : inventory){
				if(itemStack!=null && itemStack.getType()!=Material.AIR){
					empty = false;
				}
			}
			if(empty) lootInventory.close();
		}
	}
}
