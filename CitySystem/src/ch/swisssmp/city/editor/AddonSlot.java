package ch.swisssmp.city.editor;

import ch.swisssmp.city.Addon;
import ch.swisssmp.city.AddonState;
import ch.swisssmp.city.Techtree;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.InfoSlot;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AddonSlot extends InfoSlot {

    private final Techtree techtree;
    private final Addon addon;

    public AddonSlot(CustomEditorView view, int slot, Techtree techtree, Addon addon) {
        super(view, slot);
        this.techtree = techtree;
        this.addon = addon;
    }

    @Override
    protected boolean isComplete() {
        return true;
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+addon.getName();
    }

    @Override
    protected List<String> getNormalDescription() {
        AddonState state = addon.getState();
        String reasonMessage = addon.getStateReasonMessage(techtree);
        List<String> result = new ArrayList<>();
        result.add(state.getColor()+state.getDisplayName());
        if(reasonMessage!=null){
            result.add("");
            result.addAll(Arrays.stream(reasonMessage.split("\n")).map(l->ChatColor.GRAY+l).collect(Collectors.toList()));
        }
        return result;
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        return addon.getType(techtree).getItemBuilder(addon.getState());
    }
}
