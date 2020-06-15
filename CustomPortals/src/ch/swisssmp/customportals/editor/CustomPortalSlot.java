package ch.swisssmp.customportals.editor;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.customportals.CustomPortal;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.PickItemSlot;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CustomPortalSlot extends PickItemSlot {

    private final CustomPortal portal;

    public CustomPortalSlot(CustomEditorView view, int slot, CustomPortal portal) {
        super(view, slot);

        this.portal = portal;
    }

    @Override
    protected ItemStack createPick() {
        return portal.getItemStack();
    }

    @Override
    protected boolean isComplete() {
        return portal.isSetupComplete();
    }

    @Override
    public String getName() {
        return ChatColor.LIGHT_PURPLE+portal.getName();
    }

    @Override
    protected List<String> getNormalDescription() {
        return portal.getItemLore();
    }

    @Override
    protected ItemStack createSlot(){
        return portal.getItemStack();
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        return null;
    }
}
