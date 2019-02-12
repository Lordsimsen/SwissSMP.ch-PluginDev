package ch.swisssmp.npc;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.npc.editor.AbstractEditor;
import ch.swisssmp.npc.editor.Editors;
import ch.swisssmp.npc.editor.base.DeleteSlot;
import ch.swisssmp.npc.event.NPCEditorOpenEvent;
import ch.swisssmp.utils.Mathf;

public class NPCEditorView extends CustomEditorView {

	private final NPCInstance npc;
	
	private Collection<AbstractEditor> editors;
	
	private NPCEditorView(Player player, NPCInstance npc){
		super(player);
		this.npc = npc;
	}
	
	@Override
	protected Inventory createInventory(){
		Collection<AbstractEditor> editors = Editors.get(this);
		NPCEditorOpenEvent event = new NPCEditorOpenEvent(this,editors);
		Bukkit.getPluginManager().callEvent(event);
		this.editors = event.getEditors();
		return Bukkit.createInventory(null, this.getInventorySize(this.editors), "NPC bearbeiten");
	}
	
	@Override
	protected Collection<EditorSlot> createSlots(){
		Collection<EditorSlot> result = new ArrayList<EditorSlot>();
		int currentSlot = 0;
		for(AbstractEditor editor : editors){
			result.addAll(editor.createSlots(currentSlot));
			currentSlot+=editor.getSlotCount();
		}
		result.add(new DeleteSlot(this,8,npc));
		return result;
	}
	
	public NPCInstance getNPC(){
		return npc;
	}
	
	private int getInventorySize(Collection<AbstractEditor> editors){
		int slots = 0;
		for(AbstractEditor editor : editors){
			slots+=editor.getSlotCount();
		}
		return this.calculateInventorySize(getRemappedSlot(slots));
	}

	@Override
	public InventoryType getType() {
		return InventoryType.CHEST;
	}
	
	public static int getRemappedSlot(int relativeSlot){
		int columns = 8;
		int row = Mathf.floorToInt(relativeSlot / (float) columns);
		return row * 9 + (relativeSlot - row * columns);
	}
	
	public static NPCEditorView open(Player player, NPCInstance npc){
		NPCEditorView editor = new NPCEditorView(player,npc);
		editor.open();
		return editor;
	}
}
