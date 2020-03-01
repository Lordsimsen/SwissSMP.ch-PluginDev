package ch.swisssmp.editor;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.utils.Mathf;

public abstract class CustomEditorView implements Listener {

	private final Player player;
	private Inventory inventory;
	
	private InventoryView view;
	
	private Collection<EditorSlot> slots;
	
	protected CustomEditorView(Player player){
		this.player = player;
	}

	protected int calculateInventorySize(int requiredSlots){
		return Mathf.ceilToInt(requiredSlots/9f) * 9;
	}
	
	private Inventory createInventory(){
		int size = getInventorySize();
		return Bukkit.createInventory(null, calculateInventorySize(size), this.getTitle());
	}
	
	protected abstract int getInventorySize();
	
	protected abstract Collection<EditorSlot> initializeEditor();
	
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
	
	public Collection<EditorSlot> getSlots(){
		return slots;
	}
	
	public Inventory getTopInventory(){
		return inventory;
	}
	
	public PlayerInventory getBottomInventory(){
		return this.player.getInventory();
	}
	
	public ItemStack getCursor(){
		return this.view!=null ? this.view.getCursor() : null;
	}
	
	public abstract String getTitle();

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		if(event.getView()!=this.view || (event.getClickedInventory()!=this.inventory && !event.isShiftClick())) return;
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
		if(event.getView()!=this.view) return;
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event){
		if(event.getView()!=this.view) return;
		this.unregisterEvents();
		onInventoryClosed(event);
	}

	protected void onInventoryClicked(InventoryClickEvent event){
		//just for overriding
	}
	protected void onInventoryClosed(InventoryCloseEvent event){
		//just for overriding
	}

	public Player getPlayer() {
		return this.player;
	}
	
	private void unregisterEvents(){
		HandlerList.unregisterAll(this);
	}
	
	protected void open(){
		Bukkit.getPluginManager().registerEvents(this, CustomEditorAPI.getInstance());
		try{
			this.slots = this.initializeEditor();
			this.inventory = this.createInventory();
		}
		catch(Exception e){
			e.printStackTrace();
			return;
		}
		this.createItems();
		this.view = this.player.openInventory(this.inventory);
	}
	
	protected void createItems(){
		if(slots==null) return;
		for(EditorSlot slot : this.slots){
			try{
				slot.createItem();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public boolean isCursorEmpty(){
		if(this.view==null) return true;
		return this.view.getCursor()==null || this.view.getCursor().getType()==Material.AIR;
	}
	
	public void clearCursorLater(){
		if(this.view==null) return;
		Bukkit.getScheduler().runTaskLater(CustomEditorAPI.getInstance(), ()->{
			this.view.setCursor(null);
		}, 1L);
	}
	
	public void setItemLager(int slot, ItemStack itemStack){
		Bukkit.getScheduler().runTaskLater(CustomEditorAPI.getInstance(), ()->{
			this.inventory.setItem(slot,itemStack);
		}, 1L);
	}
	
	public void closeLater(){
		Bukkit.getScheduler().runTaskLater(CustomEditorAPI.getInstance(), ()->{
			if(this.view==null) return;
			this.view.close();
		}, 1L);
	}
}
