package ch.swisssmp.custompaintings.editor;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.custompaintings.CustomPainting;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.PickItemSlot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PaintingDataSlot extends PickItemSlot {

    private final CustomPainting painting;

    public PaintingDataSlot(CustomEditorView view, int slot, CustomPainting painting) {
        super(view, slot);
        this.painting = painting;
    }

    @Override
    protected ItemStack createPick() {
        return painting.getItemStack();
    }

    @Override
    protected boolean isComplete() {
        return true;
    }

    @Override
    public String getName() {
        return ChatColor.RESET+painting.getDisplayName();
    }

    @Override
    protected List<String> getNormalDescription() {
        return null;
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        CustomItemBuilder result = new CustomItemBuilder();
        result.setMaterial(Material.PAINTING);
        result.setAmount(1);
        return result;
    }
}
