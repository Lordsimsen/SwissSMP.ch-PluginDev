package ch.swisssmp.camerastudio.editor;

import ch.swisssmp.camerastudio.CameraPathSequence;
import ch.swisssmp.camerastudio.CameraStudioMaterial;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.PickItemSlot;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CameraPathSequenceSlot extends PickItemSlot {

    private final CameraPathSequence sequence;

    public CameraPathSequenceSlot(CustomEditorView view, int slot, CameraPathSequence sequence) {
        super(view, slot);
        this.sequence = sequence;
    }

    @Override
    protected ItemStack createPick() {
        return sequence.getItemStack();
    }

    @Override
    protected boolean isComplete() {
        return sequence.isSetupComplete();
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+sequence.getName();
    }

    @Override
    protected List<String> getNormalDescription() {
        return null;
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        return CustomItems.getCustomItemBuilder(CameraStudioMaterial.PATH_SEQUENCE);
    }
}
