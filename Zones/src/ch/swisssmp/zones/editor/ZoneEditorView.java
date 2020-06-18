package ch.swisssmp.zones.editor;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.DeleteSlot;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.zones.Zone;
import ch.swisssmp.zones.editor.slots.ApplyToWorldEditSelectionSlot;
import ch.swisssmp.zones.editor.slots.LaunchZoneEditorSlot;
import ch.swisssmp.zones.editor.slots.UseWorldEditSelectionSlot;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class ZoneEditorView extends CustomEditorView {

    private final Zone zone;

    protected ZoneEditorView(Player player, Zone zone) {
        super(player);
        this.zone = zone;
    }

    @Override
    protected int getInventorySize() {
        return 9;
    }

    @Override
    protected Collection<EditorSlot> initializeEditor() {
        Collection<EditorSlot> slots = new ArrayList<>();
        slots.add(new LaunchZoneEditorSlot(this, 0, zone));
        slots.add(new UseWorldEditSelectionSlot(this, 1, zone));
        slots.add(new ApplyToWorldEditSelectionSlot(this, 2, zone));
        slots.add(new DeleteSlot(this, 8, zone, "Zone"));
        return slots;
    }

    @Override
    public String getTitle() {
        return zone.getName();
    }

    public static ZoneEditorView open(Player player, Zone zone){
        ZoneEditorView view = new ZoneEditorView(player, zone);
        view.open();
        return view;
    }
}
