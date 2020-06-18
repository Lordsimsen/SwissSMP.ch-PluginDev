package ch.swisssmp.camerastudio.editor;

import ch.swisssmp.camerastudio.CameraPath;
import ch.swisssmp.camerastudio.CameraStudioMaterial;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.PickItemSlot;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CameraPathSlot extends PickItemSlot {

    private final CameraPath path;

    public CameraPathSlot(CustomEditorView view, int slot, CameraPath path) {
        super(view, slot);
        this.path = path;
    }

    @Override
    protected ItemStack createPick() {
        return path.getItemStack();
    }

    @Override
    protected boolean isComplete() {
        return path.isSetupComplete();
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+path.getName();
    }

    @Override
    protected List<String> getNormalDescription() {
        return null;
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        return CustomItems.getCustomItemBuilder(CameraStudioMaterial.PATH);
    }
}
