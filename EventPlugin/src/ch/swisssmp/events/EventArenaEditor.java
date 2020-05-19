package ch.swisssmp.events;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Collection;

public class EventArenaEditor extends CustomEditorView {

    private final EventArena arena;

    protected EventArenaEditor(Player player, EventArena arena) {
        super(player);
        this.arena = arena;
    }

    @Override
    protected Collection<EditorSlot> initializeEditor() {

        Collection<EditorSlot> result = new ArrayList<EditorSlot>();

        result.add(new ArenaNameSlot(this, 0));
        result.add(new ArenaDeleteSlot(this, 17, this.arena));

        return result;
    }

    public EventArena getArena() {
        return this.arena;
    }

    public static EventArenaEditor open(Player player, EventArena arena){
        EventArenaEditor editor = new EventArenaEditor(player, arena);
        editor.open();
        return editor;
    }

    @Override
    protected void onInventoryClicked(InventoryClickEvent arg0){
        this.arena.updateTokens();
    }

    @Override
    protected int getInventorySize() {
        return 18;
    }

    @Override
    public String getTitle() {
        return this.arena.getName();
    }
}
