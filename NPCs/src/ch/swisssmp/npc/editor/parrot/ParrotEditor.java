package ch.swisssmp.npc.editor.parrot;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.entity.Parrot;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.npc.editor.AbstractEditor;

public class ParrotEditor extends AbstractEditor {
	
	private Parrot parrot;
	
	public ParrotEditor(CustomEditorView view, Parrot parrot) {
		super(view);
		this.parrot = parrot;
	}	
	
	@Override
	public Collection<EditorSlot> createSlots(int firstSlot){
		Collection<EditorSlot> result = new ArrayList<EditorSlot>();
		result.add(new TypeSlot(this.getView(),firstSlot,this.parrot));
		return result;
	}

	@Override
	public int getSlotCount() {
		return 2;
	}
}
