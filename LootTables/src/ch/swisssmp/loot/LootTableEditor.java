package ch.swisssmp.loot;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.PlayerInventory;

public class LootTableEditor extends InventoryView implements Listener{
	
	private final Player player;
	private final LootTable lootTable;
	private final Inventory inventory;
	private final PlayerInventory playerInventory;
	
	private LootTableEditor(Player player, LootTable lootTable){
		this.player = player;
		this.lootTable = lootTable;
		this.playerInventory = player.getInventory();
		this.inventory = Bukkit.createInventory(null, 54, this.lootTable.getName().replace('_', ' '));
		if(this.lootTable.getItems()!=null){
			this.inventory.setContents(this.lootTable.getItems());
		}
	}

	@Override
	public String getTitle() {
		return lootTable.getName()!=null ? lootTable.getName() : "Unbennanter LootTable";
	}

	@EventHandler
	private void onInventoryClose(InventoryCloseEvent event){
		if(event.getView()!=this) return;
		this.lootTable.setItems(this.inventory.getContents());
		player.sendMessage(ChatColor.GREEN+"[LootTables] '"+this.lootTable.getName()+"' aktualisiert.");
		HandlerList.unregisterAll(this);
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
	
	private void open(){
		this.player.openInventory(this);
	}
	
	public static LootTableEditor open(Player player, LootTable lootTable){
		LootTableEditor editor = new LootTableEditor(player,lootTable);
		Bukkit.getPluginManager().registerEvents(editor, LootTables.getInstance());
		editor.open();
		return editor;
	}
}
