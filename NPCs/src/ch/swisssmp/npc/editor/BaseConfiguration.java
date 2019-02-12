package ch.swisssmp.npc.editor;

import java.util.ArrayList;
import java.util.Collection;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.npc.editor.base.NameSlot;
import ch.swisssmp.npc.editor.base.NameVisibilitySlot;
import ch.swisssmp.npc.editor.base.SilentSlot;

public class BaseConfiguration extends AbstractEditor {

	private NPCInstance npc;
	
	protected BaseConfiguration(CustomEditorView view, NPCInstance npc) {
		super(view);
		this.npc = npc;
	}
	
	@Override
	public Collection<EditorSlot> createSlots(int firstSlot){
		Collection<EditorSlot> result = new ArrayList<EditorSlot>();
		result.add(new NameSlot(this.getView(),firstSlot,this.npc));
		result.add(new NameVisibilitySlot(this.getView(),firstSlot+1,this.npc));
		result.add(new SilentSlot(this.getView(),firstSlot+2,this.npc));
		return result;
	}

	@Override
	public int getSlotCount() {
		return 3;
	}
}
