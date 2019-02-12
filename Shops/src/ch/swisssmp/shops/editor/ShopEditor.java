package ch.swisssmp.shops.editor;

import java.util.ArrayList;
import java.util.Collection;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.npc.editor.AbstractEditor;
import ch.swisssmp.shops.Shop;

public class ShopEditor extends AbstractEditor {

	private final Shop shop;
	
	public ShopEditor(CustomEditorView view, Shop shop) {
		super(view);
		this.shop = shop;
	}

	@Override
	public Collection<EditorSlot> createSlots(int arg0) {
		Collection<EditorSlot> result = new ArrayList<EditorSlot>();
		result.add(new ShopSlot(this.getView(),arg0,this.shop));
		return result;
	}

	@Override
	public int getSlotCount() {
		return 1;
	}

}
