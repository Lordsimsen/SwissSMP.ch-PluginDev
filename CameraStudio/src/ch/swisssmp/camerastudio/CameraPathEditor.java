package ch.swisssmp.camerastudio;

import ch.swisssmp.camerastudio.editor.path.CommandsSlot;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.DeleteSlot;
import ch.swisssmp.editor.slot.EditorSlot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class CameraPathEditor extends CustomEditorView {

    private final CameraPath path;

    protected CameraPathEditor(Player player, CameraPath path) {
        super(player);
        this.path = path;
    }

    @Override
    protected int getInventorySize() {
        return 9;
    }

    @Override
    protected Collection<EditorSlot> initializeEditor() {
        Collection<EditorSlot> slots = new ArrayList<>();
        slots.add(new CommandsSlot(this, 0, path));
        slots.add(new DeleteSlot(this, 8, path, "Pfad").onRemove(()->path.getWorld().save()));
        return slots;
    }

    @Override
    public String getTitle() {
        return ChatColor.AQUA+path.getName();
    }

    public static CameraPathEditor open(Player player, CameraPath path){
        CameraPathEditor editor = new CameraPathEditor(player, path);
        editor.open();
        return editor;
    }
}
