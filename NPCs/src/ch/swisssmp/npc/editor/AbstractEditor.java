package ch.swisssmp.npc.editor;

import java.util.Collection;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;

public abstract class AbstractEditor {
	
	private final CustomEditorView view;
	
	public AbstractEditor(CustomEditorView view){
		this.view = view;
	}
	
	protected CustomEditorView getView(){
		return view;
	}
	
	public abstract Collection<EditorSlot> createSlots(int firstSlot);
	
	public abstract int getSlotCount();
}
