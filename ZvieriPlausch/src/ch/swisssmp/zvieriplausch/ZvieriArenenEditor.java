package ch.swisssmp.zvieriplausch;

import ch.swisssmp.utils.Mathf;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class ZvieriArenenEditor implements Listener{

	private final Player p;
	private final Inventory inventory;
	
	private InventoryView view;
	
	private ZvieriArenenEditor(Player p, Collection<ZvieriArena> arenen, String label) {
		this.p = p;
		
		int cellCount = Mathf.ceilToInt(arenen.size() / 9f)*9;
		this.inventory = Bukkit.createInventory(null,  cellCount, label);
		this.createItems(arenen);
	}
	
	private void createItems(Collection<ZvieriArena> arenen) {
		for(ZvieriArena arena : arenen) {
			ItemStack tokenStack = arena.getTokenStack();
			if(tokenStack == null) {
				continue;
			}
			this.inventory.addItem(tokenStack);
		}
	}
	
	@EventHandler
	private void onInventoryClose(InventoryCloseEvent e) {
		if(e.getView() != this.view) {
			return;
		}
		HandlerList.unregisterAll(this);
	}
	
	public HumanEntity getPlayer() {
		return this.p;
	}
	
	private void open() {
		this.view = this.getPlayer().openInventory(this.inventory);
	}
	
	public static ZvieriArenenEditor open(Player p, boolean showAll) {
		ZvieriArenenEditor editor;
		if(showAll) {
			if(ZvieriArenen.getAll().size() == 0) {
				p.sendMessage(ZvieriGamePlugin.getPrefix() + " Es wurde noch keine Arena erstellt. "
						+ "Verwende /zvieriarena create [Name] um eine Arena zu erstellen");
				return null;
			}
			editor = new ZvieriArenenEditor(p, ZvieriArenen.getAll(), "Alle Arenen");
		} else {
			Collection<ZvieriArena> arenen = ZvieriArenen.get(p.getWorld());
			if(arenen.size() == 0) {
				p.sendMessage(ZvieriGamePlugin.getPrefix() + " In dieser Welt gibt es keine Arenen. "
						+ "Verwende /zvieriarenen um alle geladenen Arenen anzuzeigen");
				return null;
			}
			editor = new ZvieriArenenEditor(p, arenen, "Arenen in " + p.getWorld().getName());
		}
		Bukkit.getPluginManager().registerEvents(editor,  ZvieriGamePlugin.getInstance());
		editor.open();
		return editor;
	}

}
