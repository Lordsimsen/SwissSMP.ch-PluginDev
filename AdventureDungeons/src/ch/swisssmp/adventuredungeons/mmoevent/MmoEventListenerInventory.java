package ch.swisssmp.adventuredungeons.mmoevent;

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
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.adventuredungeons.Main;
import ch.swisssmp.adventuredungeons.mmoblock.MmoBlock;
import ch.swisssmp.adventuredungeons.mmoitem.MmoLootInventory;
import ch.swisssmp.adventuredungeons.mmoworld.MmoWorld;
import ch.swisssmp.adventuredungeons.mmoworld.MmoWorldInstance;
import ch.swisssmp.adventuredungeons.util.MmoResourceManager;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class MmoEventListenerInventory extends MmoEventListener{
	public MmoEventListenerInventory(JavaPlugin plugin) {
		super(plugin);
	}
	@EventHandler
	private void onInventoryOpen(InventoryOpenEvent event){
		HumanEntity player = event.getPlayer();
		if(player.getGameMode()!=GameMode.ADVENTURE) return;
		Inventory inventory = event.getInventory();
		if(inventory instanceof PlayerInventory)
			return;
		InventoryHolder holder = inventory.getHolder();
		if(holder instanceof Chest || holder instanceof DoubleChest || holder instanceof Furnace || holder instanceof Dispenser || holder instanceof Dropper || holder instanceof BrewingStand){
			String action;
			Location location;
			if(holder instanceof Chest){
				Main.info("opening Chest");
				Chest chest = (Chest) holder;
				location = chest.getLocation();
				action = "onchestopen";
			}
			else if(holder instanceof DoubleChest){
				Main.info("opening DoubleChest");
				DoubleChest chest = (DoubleChest) holder;
				location = chest.getLocation();
				action = "onchestopen";
			}
			else if(holder instanceof Furnace){
				Main.info("opening Furnace");
				Furnace furnace = (Furnace) holder;
				location = furnace.getLocation();
				action = "onfurnaceopen";
			}
			else if(holder instanceof Dispenser){
				Main.info("opening Dispenser");
				Dispenser dispenser = (Dispenser) holder;
				location = dispenser.getLocation();
				action = "ondispenseropen";
			}
			else if(holder instanceof Dropper){
				Main.info("opening Dropper");
				Dropper dropper = (Dropper) holder;
				location = dropper.getLocation();
				action = "ondropperopen";
			}
			else if(holder instanceof BrewingStand){
				Main.info("opening BrewingStand");
				BrewingStand brewingStand = (BrewingStand) holder;
				location = brewingStand.getLocation();
				action = "onbrewingopen";
			}
			else return;
			MmoLootInventory lootInventory = MmoLootInventory.get((Player)event.getPlayer(), action, location.getBlock());
			if(lootInventory!=null){
				Main.info("opening LootInventory from Cache");
				event.getPlayer().openInventory(lootInventory.inventory);
				event.setCancelled(true);
			}
			else{
				Main.info("attempting to open new LootInventory");
				MmoWorldInstance worldInstance = MmoWorld.getInstance(location);
				YamlConfiguration yamlResponse = DataSource.getYamlResponse("treasure.php", new String[]{
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
