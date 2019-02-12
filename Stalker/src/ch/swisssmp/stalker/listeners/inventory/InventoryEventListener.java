package ch.swisssmp.stalker.listeners.inventory;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ch.swisssmp.stalker.LogEntry;
import ch.swisssmp.stalker.Stalker;
import ch.swisssmp.utils.SwissSMPUtils;

public class InventoryEventListener implements Listener {
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
	private void onBrew(BrewEvent event){
		LogEntry logEntry = new LogEntry(event.getBlock());
		logEntry.setWhat("BREW");
		logEntry.setWhere(event.getBlock());
		JsonObject extraData = new JsonObject();
		JsonArray itemsArray = new JsonArray();
		for(ItemStack itemStack : event.getContents().getContents()){
			if(itemStack==null) continue;
			itemsArray.add(SwissSMPUtils.encodeItemStack(itemStack));
		}
		extraData.add("contents", itemsArray);
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
	private void onFurnaceSmelt(FurnaceSmeltEvent event){
		LogEntry logEntry = new LogEntry(event.getBlock());
		logEntry.setWhat("FURNACE_SMELT");
		logEntry.setWhere(event.getBlock());
		JsonObject extraData = new JsonObject();
		extraData.addProperty("item", SwissSMPUtils.encodeItemStack(event.getResult()));
		extraData.addProperty("source", SwissSMPUtils.encodeItemStack(event.getSource()));
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
	private void onInventoryOpen(InventoryOpenEvent event){
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("INVENTORY_OPEN");
		InventoryHolder holder = event.getInventory().getHolder();
		Block block = Stalker.getBlock(holder);
		logEntry.setWhere(block != null ? block : event.getPlayer().getLocation().getBlock());
		String holderName = Stalker.getIdentifier(holder);
		JsonObject extraData = new JsonObject();
		extraData.addProperty("inventory", event.getInventory().getName());
		extraData.addProperty("holder", holderName);
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onInventoryClose(InventoryCloseEvent event){
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("INVENTORY_CLOSE");
		InventoryHolder holder = event.getInventory().getHolder();
		Block block = Stalker.getBlock(holder);
		logEntry.setWhere(block != null ? block : event.getPlayer().getLocation().getBlock());
		String holderName = Stalker.getIdentifier(holder);
		JsonObject extraData = new JsonObject();
		extraData.addProperty("inventory", event.getInventory().getName());
		extraData.addProperty("holder", holderName);
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
	private void onInventoryPickupItem(InventoryPickupItemEvent event){
		LogEntry logEntry = new LogEntry(event.getInventory().getName());
		logEntry.setWhat("INVENTORY_PICKUP_ITEM");
		InventoryHolder holder = event.getInventory().getHolder();
		logEntry.setWhere(Stalker.getBlock(holder));
		String holderName = Stalker.getIdentifier(holder);
		JsonObject extraData = new JsonObject();
		extraData.addProperty("inventory", event.getInventory().getName());
		extraData.addProperty("holder", holderName);
		extraData.addProperty("item", SwissSMPUtils.encodeItemStack(event.getItem().getItemStack()));
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
	private void onInventoryMoveItem(InventoryMoveItemEvent event){
		LogEntry logEntry = new LogEntry(event.getInitiator().getName());
		logEntry.setWhat("INVENTORY_MOVE_ITEM");
		InventoryHolder holder = event.getInitiator().getHolder();
		logEntry.setWhere(Stalker.getBlock(holder));
		String holderName = Stalker.getIdentifier(holder);
		JsonObject extraData = new JsonObject();
		extraData.addProperty("source", event.getSource().getName());
		extraData.addProperty("destination", event.getDestination().getName());
		extraData.addProperty("initiator", holderName);
		extraData.addProperty("item", SwissSMPUtils.encodeItemStack(event.getItem()));
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}

    /**
     * Handle inventory transfers
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryDrag(final InventoryDragEvent event) {
        // Get container
        final InventoryHolder ih = event.getInventory().getHolder();
        Location containerLoc = null;
        if( ih instanceof BlockState ) {
            final BlockState eventChest = (BlockState) ih;
            containerLoc = eventChest.getLocation();
        }

        // Store some info
        final Player player = (Player) event.getWhoClicked();

        final Map<Integer, ItemStack> newItems = event.getNewItems();
        for ( final Entry<Integer, ItemStack> entry : newItems.entrySet() ) {
            recordInvAction( player, containerLoc, entry.getValue(), entry.getKey(), "ITEM_INSERT" );
        }
    }

    /**
     * Handle inventory transfers
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(final InventoryClickEvent event) {

        Location containerLoc = null;

        // Store some info
        final Player player = (Player) event.getWhoClicked();
        final ItemStack currentitem = event.getCurrentItem();
        final ItemStack cursoritem = event.getCursor();

        // Get location
        if( event.getInventory().getHolder() instanceof BlockState ) {
            final BlockState b = (BlockState) event.getInventory().getHolder();
            containerLoc = b.getLocation();
        } else if( event.getInventory().getHolder() instanceof Entity ) {
            final Entity e = (Entity) event.getInventory().getHolder();
            containerLoc = e.getLocation();
        } else if( event.getInventory().getHolder() instanceof DoubleChest ) {
            final DoubleChest chest = (DoubleChest) event.getInventory().getHolder();
            containerLoc = chest.getLocation();
        }
        
        // Double chests report 27 default size, though they actually
        // have 6 rows of 9 for 54 slots
        int defaultSize = event.getView().getType().getDefaultSize();
        if( event.getInventory().getHolder() instanceof DoubleChest ){
            defaultSize = event.getView().getType().getDefaultSize() * 2;
        }

        // Click in the block inventory produces slot/rawslot that are equal, only until the slot numbers exceed the
        // slot count of the inventory. At that point, they represent the player inv.
        if( event.getSlot() == event.getRawSlot() && event.getRawSlot() <= defaultSize ) {
            ItemStack addStack = null;
            ItemStack removeStack = null;

            if( currentitem != null && !currentitem.getType().equals( Material.AIR ) && cursoritem != null
                    && !cursoritem.getType().equals( Material.AIR ) ) {
                // If BOTH items are not air then you've swapped an item. We need to
                // record an insert for the cursor item and
                // and remove for the current.

                if (currentitem.isSimilar(cursoritem)) {
                    // Items are similar enough to stack
                    int amount = cursoritem.getAmount();

                    if(event.isRightClick()) {
                        amount = 1;
                    }

                    int remaining = (currentitem.getMaxStackSize() - currentitem.getAmount());
                    int inserted = (amount <= remaining) ? amount : remaining;

                    if (inserted > 0) {
                        addStack = cursoritem.clone();
                        addStack.setAmount(inserted);
                    }
                } else {
                    // Items are not similar
                    addStack = cursoritem.clone();
                    removeStack = currentitem.clone();
                }
            } else if( currentitem != null && !currentitem.getType().equals( Material.AIR ) ) {
                removeStack = currentitem.clone();
            } else if( cursoritem != null && !cursoritem.getType().equals( Material.AIR ) ) {
                addStack = cursoritem.clone();
            }

            // Record events
            if (addStack != null) {
                recordInventoryAction( player, containerLoc, addStack, event.getRawSlot(), "ITEM_INSERT", event );
            }
            if (removeStack != null) {
                recordInventoryAction( player, containerLoc, removeStack, event.getRawSlot(), "ITEM_REMOVE", event );
            }
            return;
        }
        if( event.isShiftClick() && cursoritem != null && cursoritem.getType().equals( Material.AIR ) ) {
            recordInventoryAction( player, containerLoc, currentitem, -1, "ITEM_INSERT", event );
        }
    }

    /**
     * 
     * @param player
     * @param item
     * @param slot
     * @param actionType
     */
    protected void recordInvAction(Player player, Location containerLoc, ItemStack item, int slot, String actionType) {
        recordInventoryAction( player, containerLoc, item, slot, actionType, null );
    }

    /**
     * 
     * @param player
     * @param item
     * @param slot
     * @param actionType
     */
    protected void recordInventoryAction(Player player, Location containerLoc, ItemStack item, int slot, String actionType,
            InventoryClickEvent event) {

        // Determine correct quantity. Right-click events change the item
        // quantity but don't seem to update the cursor/current items.
        int officialQuantity = 0;
        if( item != null ) {
            officialQuantity = item.getAmount();
            // If the player right-clicked we need to assume the amount
            if( event != null && event.isRightClick() ) {
                // If you're right-clicking to remove an item, it divides by two
                if( actionType.equals( "item-remove" ) ) {
                    officialQuantity = ( officialQuantity - (int) Math.floor( ( item.getAmount() / 2 ) ) );
                }
                // If you're right-clicking to insert, it's only one
                else if( actionType.equals( "item-insert" ) ) {
                    officialQuantity = 1;
                }
            }
        }

        // Record it!
        if( actionType != null && containerLoc != null && item != null && item.getType() != Material.AIR && officialQuantity > 0 ) {
        	LogEntry logEntry = new LogEntry(player);
        	logEntry.setWhat(actionType);
        	logEntry.setWhere(containerLoc.getBlock());
        	logEntry.setCurrent(containerLoc.getBlock());
        	JsonObject extraData = new JsonObject();
        	extraData.addProperty("item", SwissSMPUtils.encodeItemStack(item));
        	extraData.addProperty("amount", officialQuantity);
        	logEntry.setExtraData(extraData);
        	Stalker.log(logEntry);
        }
    }
}
