package ch.swisssmp.zvierigame;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.zvierigame.editorslots.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Collection;

public class ZvieriArenaEditor extends CustomEditorView {
	
	private final ZvieriArena arena;
	
	private ZvieriArenaEditor(Player p, ZvieriArena arena) {
		super(p);
		this.arena = arena;
	}

	@Override
	protected Collection<EditorSlot> initializeEditor() {
		
		Collection<EditorSlot> result = new ArrayList<EditorSlot>();
		
		result.add(new ArenaNameSlot(this, 0));
		result.add(new ChefSlot(this, 1));
		result.add(new LogisticsSlot(this, 2));
		result.add(new KitchenWaypointSlot(this, 3));
		result.add(new QueueWaypointSlot(this, 4));
		result.add(new CounterWaypointSlot(this, 5));
		result.add(new StorageWaypointSlot(this, 6));
		result.add(new CountersDeleteSlot(this, 14, this.arena));
		result.add(new ArenaDeleteSlot(this, 17, this.arena));
		
		return result;
	}
	
	public ZvieriArena getArena() {
		return arena;
	}
	
	public static ZvieriArenaEditor open(Player player, ZvieriArena arena) {
		ZvieriArenaEditor editor = new ZvieriArenaEditor(player, arena);
		editor.open();
		return editor;
	}
	
	@Override
	protected void onInventoryClicked(InventoryClickEvent arg0){
		this.arena.updateTokens();
	}
	
	@Override
	protected int getInventorySize() {
		return 18;
	}

	@Override
	public String getTitle() {
		return this.arena.getName();
	}
}
