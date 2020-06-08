package ch.swisssmp.camerastudio.editor.sequence;

import ch.swisssmp.camerastudio.CameraPathSequence;
import ch.swisssmp.camerastudio.CameraPathSequenceEditor;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ButtonSlot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class PlaySlot extends ButtonSlot {

    private final CameraPathSequenceEditor editor;
    private final CameraPathSequence sequence;

    public PlaySlot(CameraPathSequenceEditor view, int slot, CameraPathSequence sequence) {
        super(view, slot);
        this.editor = view;
        this.sequence = sequence;
    }

    @Override
    protected void triggerOnClick(ClickType clickType) {
        editor.savePathSequence();
        sequence.run(getView().getPlayer());
    }

    @Override
    protected boolean isComplete() {
        return sequence.isSetupComplete();
    }

    @Override
    public String getName() {
        return ChatColor.WHITE+"Pfad-Sequenz ansehen";
    }

    @Override
    protected List<String> getNormalDescription() {
        return null;
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        CustomItemBuilder result = new CustomItemBuilder();
        result.setMaterial(Material.ENDER_EYE);
        result.setAmount(1);
        return result;
    }
}
