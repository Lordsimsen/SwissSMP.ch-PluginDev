package ch.swisssmp.warehouse;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.utils.SwissSMPler;

public class EventListener implements Listener {
	
	//add resourcepack
	@EventHandler
	private void onResourcepackUpdate(PlayerResourcePackUpdateEvent event){
		event.addComponent("lagerhaus");
	}
	
	@EventHandler
	private void onInventoryOpen(InventoryOpenEvent event){
		ItemManager.updateWarehouseTools(event.getInventory());
	}
	
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event){
		if(event.getAction()!=Action.RIGHT_CLICK_AIR && event.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK && 
				(event.getClickedBlock().getType()==Material.CHEST || event.getClickedBlock().getType()==Material.TRAPPED_CHEST)) return;
		if(!event.getPlayer().hasPermission("warehouse.use")) return;
		ItemStack itemStack = event.getItem();
		if(itemStack==null) return;
		StockLedgerInfo itemInfo = StockLedgerInfo.get(itemStack);
		if(itemInfo==null) return;
		itemInfo.apply(itemStack);
		if(itemInfo.getMaster()!=null){
			itemInfo.getMaster().highlightChests();
		}
	}
	
	@EventHandler
	private void onChestInteract(PlayerInteractEvent event){
		if(event.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		Block block = event.getClickedBlock();
		if(block.getType()!=Material.CHEST && block.getType()!=Material.TRAPPED_CHEST) return;
		if(!event.getPlayer().hasPermission("warehouse.use")) return;
		ItemStack itemStack = event.getItem();
		if(itemStack==null) return;
		StockLedgerInfo itemInfo = StockLedgerInfo.get(itemStack);
		if(itemInfo==null) return;
		if(event.getPlayer().isSneaking()){
			Slave slave = Slave.get(block);
			if(slave==null) slave = Slave.create(block);
			slave.openEditor(event.getPlayer());
			event.setCancelled(true);
			return;
		}
		StockLedgerInfo chestInfo = StockLedgerInfo.get(block);

		event.setCancelled(true);
		//link stock ledger to existing master
		if(itemInfo.getMaster()==null && chestInfo!=null && chestInfo.getMaster()!=null){
			itemInfo.setId(chestInfo.getId());
			itemInfo.setMaster(chestInfo.getMaster());
			itemInfo.apply(itemStack);
			SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.GREEN+"Lagerbuch zugewiesen!");
			return;
		}
		else if(itemInfo.getMaster()==null){
			Master master = Master.create(event.getPlayer(), block);
			if(master==null){
				SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.RED+"Konnte Lagersystem nicht erstellen.");
				return;
			}
			itemInfo.setMaster(master);
			itemInfo.apply(itemStack);
			SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.GREEN+"Lagersystem erstellt!");
			return;
		}
		//configure settings
		else if(itemInfo.getMaster()!=null && chestInfo!=null && chestInfo.getMaster()!=null && itemInfo.getId().equals(chestInfo.getId())){
			MasterFilterView.open(event.getPlayer(), itemInfo.getMaster());
			return;
		}
		//add slave
		else if(itemInfo.getMaster()!=null){
			Master master = itemInfo.getMaster();
			Slave slave = Slave.get(block);
			if(slave!=null && master.getSlaves().contains(slave)){
				master.removeSlave(slave);
				SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.GRAY+"Truhe entfernt!");
			}
			else{
				if(slave==null) slave = Slave.create(block);
				if(master.addSlave(slave)){
					SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.YELLOW+"Truhe zugewiesen!");
				}
				else{
					SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.RED+"Truhe ausser Reichweite.");
				}
			}
			return;
		}
	}
	
	//trigger master chests
	@EventHandler
	private void onRedstone(BlockRedstoneEvent event){
		if(event.getNewCurrent()==0) return;
		final BlockFace[] neighbourDirections = new BlockFace[]{BlockFace.UP,BlockFace.NORTH,BlockFace.EAST,BlockFace.SOUTH,BlockFace.WEST,BlockFace.DOWN};
		Block block = event.getBlock();
		HashSet<Block> poweredBlocks = new HashSet<Block>();
		for(BlockFace face : neighbourDirections){
			if(face==BlockFace.UP && block.getState().getData() instanceof Directional && ((Directional)block.getState().getData()).getFacing()!=BlockFace.UP) continue;
			Block neighbour = block.getRelative(face);
			poweredBlocks.add(neighbour);
			if(!neighbour.getType().isSolid()) continue;
			for(BlockFace neighbourFace : neighbourDirections){
				poweredBlocks.add(neighbour.getRelative(neighbourFace));
			}
		}
		boolean animate = MaterialUtil.isPlayerInput(block.getType());
		for(Block powered : poweredBlocks){
			if(powered.getType()!=Material.CHEST && powered.getType()!=Material.TRAPPED_CHEST) continue;
			MasterChestsTriggerRoutine.addBlock(powered,animate);
		}
	}
	
	//add stock_leder id
	@EventHandler
	private void onPrepareItemCraft(PrepareItemCraftEvent event){
		CraftingInventory inventory = event.getInventory();
		ItemStack result = inventory.getResult();
		if(result==null) return;
		String custom_enum = CustomItems.getCustomEnum(result);
		if(custom_enum==null || !custom_enum.toLowerCase().equals("stock_ledger")) return;
		if(!event.getView().getPlayer().hasPermission("warehouse.use")){
			inventory.setResult(null);
		}
	}
	
	//add connected chests
	@EventHandler(ignoreCancelled=true,priority=EventPriority.MONITOR)
	private void onBlockPlace(BlockPlaceEvent event){
		if(event.getBlock().getType()!=Material.CHEST && event.getBlock().getType()!=Material.TRAPPED_CHEST) return;
		Bukkit.getScheduler().runTaskLater(WarehousesPlugin.getInstance(), ()->{
			linkDoubleChest(event);
		}, 1L);
	}
	
	private void linkDoubleChest(BlockPlaceEvent event){
		DoubleChest doubleChest = ChestUtility.getDoubleChest(event.getBlock());
		if(doubleChest==null) return;
		BlockState left = (BlockState) doubleChest.getLeftSide();
		BlockState right = (BlockState) doubleChest.getRightSide();
		Master leftWarehouse = Master.get(left.getBlock());
		Master rightWarehouse = Master.get(right.getBlock());
		Slave leftFilter = Slave.get(left.getBlock());
		Slave rightFilter = Slave.get(right.getBlock());
		if(leftWarehouse!=null) leftWarehouse.addChest(right.getBlock());
		else if(rightWarehouse!=null) rightWarehouse.addChest(left.getBlock());
		else if(leftFilter!=null) leftFilter.addChest(right.getBlock());
		else if(rightFilter!=null) rightFilter.addChest(left.getBlock());;
	}
	
	//remove chests
	@EventHandler(ignoreCancelled=true,priority=EventPriority.MONITOR)
	private void onBlockBreak(BlockBreakEvent event){
		if(event.getBlock().getType()!=Material.CHEST && event.getBlock().getType()!=Material.TRAPPED_CHEST) return;
		Block block = event.getBlock();
		Master warehouse = Master.get(block);
		if(warehouse!=null){
			warehouse.removeChest(block,false);
		}
		Slave slave = Slave.get(block);
		if(slave!=null){
			slave.removeChest(block,false);
		}
	}
	
	//load systems
	@EventHandler
	private void onWorldLoad(WorldLoadEvent event){
		SlaveCollections.loadCollection(event.getWorld());
		MasterCollections.loadCollection(event.getWorld());
	}
	
	//save systems
	@EventHandler
	private void onWorldSave(WorldSaveEvent event){
		SlaveCollections.saveCollection(event.getWorld());
		MasterCollections.saveCollection(event.getWorld());
	}
	
	//unload systems
	@EventHandler
	private void onWorldUnload(WorldUnloadEvent event){
		SlaveCollections.unloadCollection(event.getWorld(), true);
		MasterCollections.unloadCollection(event.getWorld(), true);
	}
}
