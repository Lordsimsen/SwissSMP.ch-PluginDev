package ch.swisssmp.zones.editor.slots;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ButtonSlot;
import ch.swisssmp.zones.Zone;
import ch.swisssmp.zones.editor.ZoneEditor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class LaunchZoneEditorSlot extends ButtonSlot {

    private final Zone zone;

    public LaunchZoneEditorSlot(CustomEditorView view, int slot, Zone zone) {
        super(view, slot);
        this.zone = zone;
    }

    @Override
    protected void triggerOnClick(ClickType clickType) {
        getView().closeLater();
        ZoneEditor.start(getView().getPlayer(), zone);
    }

    @Override
    protected boolean isComplete() {
        return zone.isSetupComplete();
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+"Auswahl-Modus starten";
    }

    @Override
    protected List<String> getNormalDescription() {
        return null;
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        CustomItemBuilder result = new CustomItemBuilder();
        result.setMaterial(Material.WRITTEN_BOOK);
        result.setAmount(1);
        return result;
    }
}
