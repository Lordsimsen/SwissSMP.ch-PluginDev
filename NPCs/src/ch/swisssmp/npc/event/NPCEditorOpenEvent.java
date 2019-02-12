package ch.swisssmp.npc.event;

import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import ch.swisssmp.npc.NPCEditorView;
import ch.swisssmp.npc.editor.AbstractEditor;

public class NPCEditorOpenEvent extends PlayerNPCEvent {
    private static final HandlerList handlers = new HandlerList();
	
    private final NPCEditorView editor;
    private final Collection<AbstractEditor> editors;
    
	public NPCEditorOpenEvent(NPCEditorView editor, Collection<AbstractEditor> editors) {
		super((Player)editor.getPlayer(),editor.getNPC());
		this.editor = editor;
		this.editors = editors;
	}
	
	public NPCEditorView getView(){
		return editor;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}
	
	public Collection<AbstractEditor> getEditors(){
		return editors;
	}
}
