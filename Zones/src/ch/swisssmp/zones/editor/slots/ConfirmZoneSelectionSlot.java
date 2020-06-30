package ch.swisssmp.zones.editor.slots;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ButtonSlot;
import ch.swisssmp.zones.editor.ZoneEditor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.List;

public class ConfirmZoneSelectionSlot extends ButtonSlot {

    private final ZoneEditor editor;

    public ConfirmZoneSelectionSlot(CustomEditorView view, int slot, ZoneEditor editor) {
        super(view, slot);
        this.editor = editor;
    }

    @Override
    protected void triggerOnClick(ClickType clickType) {
        getView().closeLater(editor::complete);
    }

    @Override
    protected boolean isComplete() {
        return true;
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+"Auswahl bestätigen";
    }

    @Override
    protected List<String> getNormalDescription() {
        return Arrays.asList(ChatColor.GRAY+"Beendet den Auswahl-",ChatColor.GRAY+"Modus und speichert",ChatColor.GRAY+"die Zone.");
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        return new CustomItemBuilder(Material.FEATHER);
    }
}
