package ch.swisssmp.dungeongenerator;

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

import ch.swisssmp.utils.InventoryUtil;
import ch.swisssmp.utils.ItemUtil;

public class GeneratorsView extends InventoryView implements Listener {
	private final Player player;
	private final Inventory inventory;
	
	private GeneratorsView(Player player){
		this.player = player;
		this.inventory = Bukkit.createInventory(null, 36, "Generatoren");
		this.createGeneratorTokens();
	}
	
	private void createGeneratorTokens(){
		GeneratorManager manager = GeneratorManager.get(player.getWorld());
		if(manager==null) return;
		for(DungeonGenerator generator : manager.getAll()){
			this.inventory.addItem(ItemManager.getInventoryToken(generator, 1));
		}
	}

	@Override
	public String getTitle() {
		return "Generatoren";
	}

	/**
	 * Allow taking items and refill them instantly
	 * Allow putting back items by clearing the underlying slot
	 * In this event an ItemStack may be in the slot and in the cursor at the same time due to server-client communication constraints
	 */
	@EventHandler
	private void onInventoryClick(InventoryClickEvent event){
		if(event.getView()!=this || event.getClickedInventory()!=this.inventory) return;
		ItemStack itemStack = this.inventory.getItem(event.getSlot());
		if(itemStack==null){
			Bukkit.getScheduler().runTaskLater(DungeonGeneratorPlugin.getInstance(), ()->{
				inventory.setItem(event.getSlot(), null);
			}, 1L);
			return;
		}
		if(itemStack!=null && event.getCursor()!=null && event.getCursor().getType()!=Material.AIR && itemStack!=event.getCursor()){
			int generator_id = ItemUtil.getInt(itemStack, "generator_id");
			if(generator_id==ItemUtil.getInt(event.getCursor(), "generator_id")){
				itemStack.setAmount(0);
				return;
			}
			event.setCancelled(true);
		}
		InventoryUtil.refillInventorySlot(inventory, event.getSlot(), itemStack.clone());
		
	}
	
	@EventHandler
	private void onInventoryDrag(InventoryDragEvent event){
		if(event.getView()!=this) return;
		event.setCancelled(true);
	}
	
	@EventHandler
	private void onInventoryClose(InventoryCloseEvent event){
		if(event.getView()!=this) return;
		HandlerList.unregisterAll(this);
	}

	@Override
	public Inventory getBottomInventory() {
		return this.player.getInventory();
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
	
	protected static GeneratorsView open(Player player){
		GeneratorsView result = new GeneratorsView(player);
		Bukkit.getPluginManager().registerEvents(result, DungeonGeneratorPlugin.getInstance());
		player.openInventory(result);
		return result;
	}
}
