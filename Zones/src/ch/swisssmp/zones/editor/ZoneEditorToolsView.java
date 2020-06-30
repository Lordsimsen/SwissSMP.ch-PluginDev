package ch.swisssmp.zones.editor;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;

public class ZoneEditorToolsView extends CustomEditorView {

    private final ZoneEditor editor;

    protected ZoneEditorToolsView(ZoneEditor editor) {
        super(editor.getPlayer());
        this.editor = editor;
    }

    @Override
    protected int getInventorySize() {
        return 9;
    }

    @Override
    protected Collection<EditorSlot> initializeEditor() {
        return Arrays.asList(

        );
    }

    @Override
    public String getTitle() {
        return "Zonen-Werkzeuge";
    }
}
