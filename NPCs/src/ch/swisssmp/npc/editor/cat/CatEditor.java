package ch.swisssmp.npc.editor.cat;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.entity.Cat;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.npc.editor.AbstractEditor;

public class CatEditor extends AbstractEditor {
	
	private Cat cat;
	
	public CatEditor(CustomEditorView view, Cat cat) {
		super(view);
		this.cat = cat;
	}	
	
	@Override
	public Collection<EditorSlot> createSlots(int firstSlot){
		Collection<EditorSlot> result = new ArrayList<EditorSlot>();
		result.add(new TypeSlot(this.getView(),firstSlot,this.cat));
		return result;
	}

	@Override
	public int getSlotCount() {
		return 2;
	}
}
