package ch.swisssmp.knightstournament;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.Mathf;

public class KnightsArenasEditor implements Listener{
	
	private final Player p;
	private final Inventory inventory;
	
	private InventoryView view;
	
	private KnightsArenasEditor(Player p, World world) {
		this(p, KnightsArena.getLoadedArenas(), "Arenen in " + world.getName().replace('_', ' '));
	}
	
	private KnightsArenasEditor(Player p, Collection<KnightsArena> arenen, String label) {
		this.p = p;
		
		int cellCount = Mathf.ceilToInt(arenen.size() / 9f)*9;
		this.inventory = Bukkit.createInventory(null,  cellCount, label);
		this.createItems(arenen);
	}
	
	private void createItems(Collection<KnightsArena> arenen) {
		for(KnightsArena arena : arenen) {
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
	
	public static KnightsArenasEditor open(Player p) {
		KnightsArenasEditor editor;
		
//		if(showAll) {
//			if(KnightsArena.getLoadedArenas().size() == 0) {
//				p.sendMessage(KnightsTournamentPlugin.prefix + " Es wurde noch keine Arena erstellt. "
//						+ "Verwende /knightsarena create [Name] um eine Arena zu erstellen");
//				return null;
//			}
//			editor = new KnightsArenasEditor(p, KnightsArena.getLoadedArenas(), "Alle Arenen");
//		} else {
		
		Collection<KnightsArena> arenen = KnightsArena.getLoadedArenas();
		if(arenen.size() == 0) {
			p.sendMessage(KnightsTournamentPlugin.prefix + "In dieser Welt gibt es keine Arenen"
					+ "Verwende /knightsarena create [name] um eine Arena zu erstellen");
			return null;
		}
		editor = new KnightsArenasEditor(p, arenen, "Arenen in " + p.getWorld().getName());
		
		Bukkit.getPluginManager().registerEvents(editor,  KnightsTournamentPlugin.getInstance());
		editor.open();
		return editor;
	}

}
