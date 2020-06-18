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

public class TravelSoundSlot extends ValueSlot {

    private final CustomPortal portal;

    public TravelSoundSlot(CustomEditorView view, int slot, CustomPortal portal) {
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
        this.portal.setTravelSound(value);
        this.portal.getContainer().save();
        this.portal.updateTokens();
        return true;
    }

    @Override
    protected boolean isComplete() {
        return portal.getTravelSound()!=null;
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+"Sound";
    }

    @Override
    protected List<String> getNormalDescription() {
        List<String> lines = new ArrayList<>();
        lines.add(ChatColor.GRAY+"Namensschild mit");
        lines.add(ChatColor.GRAY+"Sound einsetzen");
        if(portal.getTravelSound()!=null) lines.add(0, ChatColor.GREEN+portal.getTravelSound());
        return lines;
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        CustomItemBuilder itemBuilder = new CustomItemBuilder();
        itemBuilder.setMaterial(Material.NAME_TAG);
        itemBuilder.setAmount(1);
        return itemBuilder;
    }
}
