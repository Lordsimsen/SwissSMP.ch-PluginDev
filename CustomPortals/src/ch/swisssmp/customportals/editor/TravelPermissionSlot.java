package ch.swisssmp.customportals.editor;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customportals.CustomPortal;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ValueSlot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TravelPermissionSlot extends ValueSlot {

    private final CustomPortal portal;

    public TravelPermissionSlot(CustomEditorView view, int slot, CustomPortal portal) {
        super(view, slot);
        this.portal = portal;
    }

    @Override
    protected ItemStack createPick() {
        return new ItemStack(Material.NAME_TAG);
    }

    @Override
    protected boolean applyValue(ItemStack itemStack) {
        String value = itemStack.getItemMeta().getDisplayName();
        if(value==null) return false;
        this.portal.setTravelPermission(value);
        this.portal.getContainer().save();
        this.portal.updateTokens();
        return true;
    }

    @Override
    protected boolean isComplete() {
        return portal.getTravelPermission()!=null;
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+"Berechtigung";
    }

    @Override
    protected List<String> getNormalDescription() {
        List<String> lines = new ArrayList<>();
        lines.add(ChatColor.GRAY+"Namensschild mit");
        lines.add(ChatColor.GRAY+"Berechtigung einsetzen");
        if(portal.getTravelPermission()!=null) lines.add(0, ChatColor.GREEN+portal.getTravelPermission());
        return lines;
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        CustomItemBuilder itemBuilder = new CustomItemBuilder();
        itemBuilder.setMaterial(Material.FEATHER);
        itemBuilder.setAmount(1);
        return itemBuilder;
    }
}
