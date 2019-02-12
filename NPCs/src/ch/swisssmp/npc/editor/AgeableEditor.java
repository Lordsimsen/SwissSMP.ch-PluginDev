package ch.swisssmp.npc.editor;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.entity.Ageable;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.npc.editor.ageable.AdultStateSlot;

public class AgeableEditor extends AbstractEditor {

	private Ageable ageable;
	
	protected AgeableEditor(CustomEditorView view, Ageable ageable) {
		super(view);
		this.ageable = ageable;
	}
	
	@Override
	public Collection<EditorSlot> createSlots(int firstSlot){
		Collection<EditorSlot> result = new ArrayList<EditorSlot>();
		result.add(new AdultStateSlot(this.getView(),firstSlot,this.ageable));
		return result;
	}

	@Override
	public int getSlotCount() {
		return 1;
	}
}
