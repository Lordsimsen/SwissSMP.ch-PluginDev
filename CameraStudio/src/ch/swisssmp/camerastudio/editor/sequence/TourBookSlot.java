package ch.swisssmp.camerastudio.editor.sequence;

import ch.swisssmp.camerastudio.CameraPathSequence;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ValueSlot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class TourBookSlot extends ValueSlot {

    private final CameraPathSequence sequence;

    public TourBookSlot(CustomEditorView view, int slot, CameraPathSequence sequence) {
        super(view, slot);
        this.sequence = sequence;
    }

    @Override
    protected ItemStack createPick() {
        return sequence.getTourBookTemplate()!=null ? sequence.getTourBookTemplate() : new ItemStack(Material.WRITABLE_BOOK);
    }

    @Override
    protected boolean applyValue(ItemStack itemStack) {
        if(itemStack==null) return false;
        if(itemStack.getType()!=Material.WRITABLE_BOOK && itemStack.getType()!=Material.WRITTEN_BOOK) return false;
        sequence.setTourBookTemplate(itemStack);
        sequence.getWorld().save();
        return true;
    }

    @Override
    protected boolean isComplete() {
        return sequence.getTourBookTemplate()!=null;
    }

    @Override
    public String getName() {
        return ChatColor.WHITE+"Beschreibungsbuch";
    }

    @Override
    protected List<String> getNormalDescription() {
        return Arrays.asList(ChatColor.GRAY+"Beschriebenes Buch",ChatColor.GRAY+"einfügen");
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        CustomItemBuilder result = new CustomItemBuilder();
        result.setMaterial(isComplete() ? Material.WRITTEN_BOOK : Material.WRITABLE_BOOK);
        result.setAmount(1);
        return result;
    }
}
