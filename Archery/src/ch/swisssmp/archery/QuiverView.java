package ch.swisssmp.archery;

import java.util.HashMap;

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
import org.bukkit.inventory.meta.ItemMeta;

public class QuiverView extends InventoryView implements Listener{
	private static HashMap<Player,QuiverView> views = new HashMap<Player,QuiverView>();
	
	private Player player;
	private ItemStack quiver;
	private Inventory quiverInventory;
	
	public QuiverView(Player player, ItemStack quiver){
		this.player = player;
		this.quiver = quiver;
		this.openQuiverInventory();
		this.fillQuiverInventory();
		Bukkit.getPluginManager().registerEvents(this, Archery.getInstance());
		views.put(player, this);
	}
	
	public void update(){
		this.fillQuiverInventory();
	}
	
	private void openQuiverInventory(){
		this.quiverInventory = Bukkit.createInventory(null, QuiverManager.getQuiverSize(), this.quiver.getItemMeta().getDisplayName());
	}
	
	private void fillQuiverInventory(){
		quiverInventory.setContents(QuiverManager.getQuiverContents(quiver));
	}
	
	private void saveQuiverInventory(){
		Bukkit.getScheduler().runTaskLater(Archery.getInstance(), new Runnable(){
			public void run(){
				QuiverManager.setQuiverContents(quiver, quiverInventory.getContents());
			}
		}, 1L);
	}

	@Override
	public String getTitle() {
		ItemMeta itemMeta = quiver.getItemMeta();
		return itemMeta!=null && itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : "Köcher";
	}

	@EventHandler
	private void onInventoryClick(InventoryClickEvent event){
		if(event.getView()!=this) return;
		//Bukkit.getLogger().info("[Archery] InventoryClick Action is "+event.getAction().toString());
		switch(event.getAction()){
		case PLACE_ALL:
		case PLACE_ONE:
		case PLACE_SOME:
		case SWAP_WITH_CURSOR:
		case HOTBAR_MOVE_AND_READD:
		case HOTBAR_SWAP:
			if(event.getClickedInventory()!=this.quiverInventory) return;
			break;
		case MOVE_TO_OTHER_INVENTORY:
			event.setCancelled(true); //this action crashes the server in custom inventories so let's just not allow it
			Inventory clicked = event.getClickedInventory();
			ItemStack itemStack = clicked.getItem(event.getSlot());
			if(itemStack==null || (itemStack.getType()!=Material.ARROW && itemStack.getType()!=Material.TIPPED_ARROW)) return;
			Inventory other = (clicked==this.quiverInventory)?this.player.getInventory():this.quiverInventory;
			HashMap<Integer,ItemStack> overflow = other.addItem(itemStack);
			if(overflow.size()>0){
				clicked.setItem(event.getSlot(), overflow.get(0));
			}
			else{
				clicked.setItem(event.getSlot(), null);
			}
			return;
		default:
			return;
		}
		if(event.getCursor()!=null && event.getCursor().getType()!=Material.ARROW && event.getCursor().getType() !=Material.TIPPED_ARROW){
			event.setCancelled(true);
			//Bukkit.getLogger().info("[Archery] Prevent placing of "+event.getCursor().getType());
			return;
		}
		this.saveQuiverInventory();
	}
	
	@EventHandler
	private void onInventoryDrag(InventoryDragEvent event){
		if(event.getView()!=this || event.getInventory()!=this.quiverInventory) return;
		if(event.getOldCursor()==null)return;
		if(event.getOldCursor().getType()!=Material.ARROW && event.getOldCursor().getType()!=Material.TIPPED_ARROW){
			event.setCancelled(true);
			return;
		}
		this.saveQuiverInventory();
	}
	
	@EventHandler
	private void onInventoryClose(InventoryCloseEvent event){
		if(event.getView()!=this) return;
		HandlerList.unregisterAll(this);
		this.saveQuiverInventory();
		views.remove(player);
	}
	
	@Override
	public Inventory getBottomInventory() {
		return player.getInventory();
	}

	@Override
	public HumanEntity getPlayer() {
		return this.player;
	}

	@Override
	public Inventory getTopInventory() {
		return this.quiverInventory;
	}

	@Override
	public InventoryType getType() {
		return InventoryType.CHEST;
	}
	
	public static QuiverView get(Player player){
		return views.get(player);
	}
}
