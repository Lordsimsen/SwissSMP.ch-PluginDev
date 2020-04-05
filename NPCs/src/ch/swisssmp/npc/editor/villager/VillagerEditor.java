package ch.swisssmp.npc.editor.villager;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.entity.Villager;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.npc.editor.AbstractEditor;

public class VillagerEditor extends AbstractEditor {
	
	private Villager villager;
	
	public VillagerEditor(CustomEditorView view, Villager villager) {
		super(view);
		this.villager = villager;
	}	
	
	@Override
	public Collection<EditorSlot> createSlots(int firstSlot){
		Collection<EditorSlot> result = new ArrayList<EditorSlot>();
		result.add(new ProfessionSlot(this.getView(),firstSlot,this.villager));
		result.add(new TypeSlot(this.getView(),firstSlot+1,this.villager));
		return result;
	}

	@Override
	public int getSlotCount() {
		return 2;
	}
}
