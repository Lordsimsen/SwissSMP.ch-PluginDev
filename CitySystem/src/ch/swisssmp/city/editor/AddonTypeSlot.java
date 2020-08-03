package ch.swisssmp.city.editor;

import ch.swisssmp.city.AddonType;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.InfoSlot;
import org.bukkit.ChatColor;

import java.util.List;

public class AddonTypeSlot extends InfoSlot {

    private final AddonType type;

    public AddonTypeSlot(CustomEditorView view, int slot, AddonType type) {
        super(view, slot);
        this.type = type;
    }

    @Override
    protected boolean isComplete() {
        return true;
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+type.getName();
    }

    @Override
    protected List<String> getNormalDescription() {
        return type.getShortDescription();
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        return type.getItemBuilder();
    }
}
