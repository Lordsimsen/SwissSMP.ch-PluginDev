package ch.swisssmp.npc.editor;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.entity.Villager;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.npc.editor.villager.ProfessionSlot;

public class VillagerEditor extends AbstractEditor {
	
	private Villager villager;
	
	protected VillagerEditor(CustomEditorView view, Villager villager) {
		super(view);
		this.villager = villager;
	}	
	
	@Override
	public Collection<EditorSlot> createSlots(int firstSlot){
		Collection<EditorSlot> result = new ArrayList<EditorSlot>();
		result.add(new ProfessionSlot(this.getView(),firstSlot,this.villager));
		return result;
	}

	@Override
	public int getSlotCount() {
		return 1;
	}
}
