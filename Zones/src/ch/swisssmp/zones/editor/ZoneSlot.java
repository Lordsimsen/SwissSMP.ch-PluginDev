package ch.swisssmp.zones.editor;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.PickItemSlot;
import ch.swisssmp.zones.Zone;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ZoneSlot extends PickItemSlot {

    private final Zone zone;

    public ZoneSlot(CustomEditorView view, int slot, Zone zone) {
        super(view, slot);
        this.zone = zone;
    }


    @Override
    protected ItemStack createPick() {
        return zone.getItemStack();
    }

    @Override
    protected boolean isComplete() {
        return zone.isSetupComplete();
    }

    @Override
    public String getName() {
        return ChatColor.WHITE+zone.getName();
    }

    @Override
    protected List<String> getNormalDescription() {
        return zone.getItemLore();
    }

    @Override
    protected ItemStack createSlot(){
        return zone.getItemStack();
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        return null;
    }
}
