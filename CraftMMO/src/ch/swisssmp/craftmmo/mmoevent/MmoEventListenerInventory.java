package ch.swisssmp.craftmmo.mmoevent;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Dropper;
import org.bukkit.block.Furnace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmoblock.MmoBlock;
import ch.swisssmp.craftmmo.mmoitem.MmoItemManager;
import ch.swisssmp.craftmmo.mmoitem.MmoLootInventory;
import ch.swisssmp.craftmmo.mmoworld.MmoWorld;
import ch.swisssmp.craftmmo.mmoworld.MmoWorldInstance;
import ch.swisssmp.craftmmo.util.MmoResourceManager;

public class MmoEventListenerInventory extends MmoEventListener{
	public MmoEventListenerInventory(JavaPlugin plugin) {
		super(plugin);
	}
	@EventHandler
	private void onInventoryOpen(InventoryOpenEvent event){
		Main.info("InventoryOpenEvent");
		Inventory inventory = event.getInventory();
		if(inventory instanceof PlayerInventory)
			return;
		InventoryHolder holder = inventory.getHolder();
		if(holder instanceof Chest || holder instanceof DoubleChest || holder instanceof Furnace || holder instanceof Dispenser || holder instanceof Dropper || holder instanceof BrewingStand){
			Main.info("opening Chest");
			Location location;
			if(holder instanceof Chest){
				Chest chest = (Chest) holder;
				location = chest.getLocation();
			}
			else if(holder instanceof DoubleChest){
				DoubleChest chest = (DoubleChest) holder;
				location = chest.getLocation();
			}
			else if(holder instanceof Furnace){
				Furnace furnace = (Furnace) holder;
				location = furnace.getLocation();
			}
			else if(holder instanceof Dispenser){
				Dispenser dispenser = (Dispenser) holder;
				location = dispenser.getLocation();
			}
			else if(holder instanceof Dropper){
				Dropper dropper = (Dropper) holder;
				location = dropper.getLocation();
			}
			else if(holder instanceof BrewingStand){
				BrewingStand brewingStand = (BrewingStand) holder;
				location = brewingStand.getLocation();
			}
			else return;
			String action = "onchestopen";
			MmoLootInventory lootInventory = MmoLootInventory.get((Player)event.getPlayer(), action, location.getBlock());
			if(lootInventory!=null){
				Main.info("opening LootInventory from Cache");
				event.getPlayer().openInventory(lootInventory.inventory);
				event.setCancelled(true);
			}
			else{
				Main.info("attempting to open new LootInventory");
				MmoWorldInstance worldInstance = MmoWorld.getInstance(location);
				YamlConfiguration yamlResponse = MmoResourceManager.getYamlResponse("treasure.php", new String[]{
						"player="+event.getPlayer().getUniqueId().toString(),
						"mc_enum="+MmoBlock.getMaterialString(location, true),
						"action="+action,
						"x="+location.getX(),
						"y="+location.getY(),
						"z="+location.getZ(),
						"world="+worldInstance.system_name,
						"world_instance="+worldInstance.world.getName()
						});
				if(yamlResponse.contains("loot")){
					Main.info("opening new LootInventory");
					MmoResourceManager.processYamlData(event.getPlayer().getUniqueId(), yamlResponse);
					event.setCancelled(true);
				}
			}
		}
		MmoItemManager.updateInventory(inventory);
	}
	@EventHandler
	private void onInventoryClose(InventoryCloseEvent event){
		Inventory inventory = event.getInventory();
		MmoLootInventory lootInventory = MmoLootInventory.get(inventory);
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
