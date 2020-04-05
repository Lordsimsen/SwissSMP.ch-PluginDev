package ch.swisssmp.ageofempires;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import ch.swisssmp.ageofempires.editor.ChangePageSlot;
import ch.swisssmp.ageofempires.editor.ToggleTauntsSlot;
import ch.swisssmp.ageofempires.editor.TriggerTauntSlot;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.utils.Mathf;

public class TauntsView extends CustomEditorView {

	private final int entriesPerPage = 8 * 6;
	
	private final TauntEntry[] entries;
	private final int maxPage;
	
	private int page = 0;
	
	protected TauntsView(Player player, TauntEntry[] entries) {
		super(player);
		this.entries = entries;
		this.maxPage = Math.max(0, Mathf.ceilToInt(entries.length / (double) entriesPerPage) - 1);
	}

	@Override
	protected int getInventorySize() {
		return 54;
	}

	@Override
	protected Collection<EditorSlot> initializeEditor() {
		List<EditorSlot> slots = new ArrayList<EditorSlot>();
		if(page>0) slots.add(new ChangePageSlot(this, 8, false));
		if(page<maxPage) slots.add(new ChangePageSlot(this, 53, true));
		slots.add(new ToggleTauntsSlot(this, 17));
		int startPosition = page * entriesPerPage;
		int maxPosition = Math.min(entriesPerPage, entries.length - startPosition);
		int entriesPerRow = 8;
		for(int i = 0; i < maxPosition; i++) {
			int row = Mathf.floorToInt(i / (double) entriesPerRow);
			int column = i - row * entriesPerRow;
			int slot = row * 9 + column;
			slots.add(new TriggerTauntSlot(this, slot, entries[i+startPosition]));
		}
		return slots;
	}
	
	public void pageUp() {
		if(page<=0) return;
		page--;
		this.recreateSlots();
	}
	
	public void pageDown() {
		if(page>=maxPage) return;
		page++;
		this.recreateSlots();
	}

	@Override
	public String getTitle() {
		return "Age Of Empires Taunts";
	}

	public static TauntsView open(Player player) {
		Collection<TauntEntry> entries = TauntEntries.getAll().stream().sorted((a,b)->a.getKey().length()!=b.getKey().length()
				? a.getKey().length() > b.getKey().length() ? 1 : -1
				: a.getKey().compareTo(b.getKey())).collect(Collectors.toList());
		TauntEntry[] entriesArray = new TauntEntry[entries.size()];
		entries.toArray(entriesArray);
		TauntsView result = new TauntsView(player, entriesArray);
		result.open();
		return result;
	}
}
