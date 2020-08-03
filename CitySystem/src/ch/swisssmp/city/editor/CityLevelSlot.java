package ch.swisssmp.city.editor;

import ch.swisssmp.city.CityLevel;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.InfoSlot;
import org.bukkit.Material;

import java.util.List;

public class CityLevelSlot extends InfoSlot {

    private final CityLevel level;

    public CityLevelSlot(CustomEditorView view, int slot, CityLevel level) {
        super(view, slot);
        this.level = level;
    }

    @Override
    protected boolean isComplete() {
        return true;
    }

    @Override
    public String getName() {
        return level.getName();
    }

    @Override
    protected List<String> getNormalDescription() {
        return null;
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        return level.getTokenBuilder();
    }
}
