package ch.swisssmp.editor;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.utils.Mathf;

public abstract class CustomEditorView extends InventoryView implements Listener {

	private final Player player;
	private Inventory inventory;
	private final PlayerInventory playerInventory;
	
	private Collection<EditorSlot> slots;
	
	protected CustomEditorView(Player player){
		this.player = player;
		this.playerInventory = player.getInventory();
	}

	protected int calculateInventorySize(int requiredSlots){
		return Mathf.ceilToInt(requiredSlots/9f) * 9;
	}
	
	protected abstract Collection<EditorSlot> createSlots();
	
	private void createItems(){
		for(EditorSlot slot : this.slots){
			try{
				slot.createItem();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	protected abstract Inventory createInventory();
	
	protected EditorSlot getSlot(int inventorySlot){
		for(EditorSlot slot : this.slots){
			if(slot.getSlot()!=inventorySlot) continue;
			return slot;
		}
		return null;
	}
	
	protected boolean allowEmptySlotInteraction(){
		return false;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		if(event.getView()!=this || (event.getClickedInventory()!=this.inventory && !event.isShiftClick())) return;
		EditorSlot editorSlot = this.getSlot(event.getSlot());
		if(editorSlot==null){
			event.setCancelled(!this.allowEmptySlotInteraction());
			return;
		}
		boolean cancelEvent = editorSlot.onClick(event.getClick());
		if(cancelEvent){
			event.setCancelled(cancelEvent);
			return;
		}
		onInventoryClicked(event);
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event){
		if(event.getView()!=this) return;
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event){
		if(event.getView()!=this) return;
		this.unregisterEvents();
		onInventoryClosed(event);
	}

	protected void onInventoryClicked(InventoryClickEvent event){
		//just for overriding
	}
	protected void onInventoryClosed(InventoryCloseEvent event){
		//just for overriding
	}

	@Override
	public Inventory getBottomInventory() {
		return this.playerInventory;
	}

	@Override
	public HumanEntity getPlayer() {
		return this.player;
	}

	@Override
	public Inventory getTopInventory() {
		return this.inventory;
	}

	@Override
	public InventoryType getType() {
		return InventoryType.CHEST;
	}
	
	private void unregisterEvents(){
		HandlerList.unregisterAll(this);
	}
	
	protected void open(){
		Bukkit.getPluginManager().registerEvents(this, CustomEditorAPI.getInstance());
		this.inventory = this.createInventory();
		if(this.inventory==null){
			return;
		}
		try{
			this.slots = this.createSlots();
		}
		catch(Exception e){
			e.printStackTrace();
			return;
		}
		this.createItems();
		this.player.openInventory(this);
	}
	
	public boolean isCursorEmpty(){
		return this.getCursor()==null || this.getCursor().getType()==Material.AIR;
	}
	
	public void clearCursorLater(){
		Bukkit.getScheduler().runTaskLater(CustomEditorAPI.getInstance(), ()->{
			this.setCursor(null);
		}, 1L);
	}
	
	public void setItemLager(int slot, ItemStack itemStack){
		Bukkit.getScheduler().runTaskLater(CustomEditorAPI.getInstance(), ()->{
			this.inventory.setItem(slot,itemStack);
		}, 1L);
	}
	
	public void closeLater(){
		Bukkit.getScheduler().runTaskLater(CustomEditorAPI.getInstance(), ()->{
			close();
		}, 1L);
	}
}
