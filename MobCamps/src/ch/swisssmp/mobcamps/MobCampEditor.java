package ch.swisssmp.mobcamps;

import org.bukkit.Bukkit;
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

public class MobCampEditor extends InventoryView implements Listener{
	private final Player player;
	private final MobCamp mobCamp;
	private final Inventory inventory;
	private final PlayerInventory playerInventory;
	
	private MobCampEditor(Player player, MobCamp mobCamp){
		this.player = player;
		this.mobCamp = mobCamp;
		this.playerInventory = player.getInventory();
		this.inventory = mobCamp.getContents();
	}
	
	@EventHandler
	private void onInventoryClose(InventoryCloseEvent event){
		if(event.getView()!=this) return;
		this.mobCamp.save();
		MobCampInstance.updateAll(this.mobCamp);
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
	
	public static MobCampEditor open(Player player, MobCamp mobCamp){
		MobCampEditor editor = new MobCampEditor(player,mobCamp);
		Bukkit.getPluginManager().registerEvents(editor, MobCamps.plugin);
		editor.open();
		return editor;
	}
}
