package ch.swisssmp.zones.editor;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.DeleteSlot;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.zones.Zone;
import ch.swisssmp.zones.editor.slots.LaunchZoneEditorSlot;
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
        return 18;
    }

    @Override
    protected Collection<EditorSlot> initializeEditor() {
        Collection<EditorSlot> slots = new ArrayList<>();
        slots.add(new LaunchZoneEditorSlot(this, 8, zone));
        slots.add(new DeleteSlot(this, 17, zone, "Zone"));
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
