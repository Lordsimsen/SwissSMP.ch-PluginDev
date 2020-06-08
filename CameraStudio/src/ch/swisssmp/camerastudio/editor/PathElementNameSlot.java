package ch.swisssmp.camerastudio.editor;

import ch.swisssmp.camerastudio.CameraPathElement;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ValueSlot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class PathElementNameSlot extends ValueSlot {

    private final CameraPathElement element;

    public PathElementNameSlot(CustomEditorView view, int slot, CameraPathElement element) {
        super(view, slot);
        this.element = element;
    }

    @Override
    protected boolean applyValue(ItemStack itemStack) {
        if(itemStack==null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()) return false;
        String displayName = itemStack.getItemMeta().getDisplayName();
        element.setName(displayName);
        return true;
    }

    @Override
    protected ItemStack createPick() {
        CustomItemBuilder itemBuilder = new CustomItemBuilder();
        itemBuilder.setAmount(1);
        itemBuilder.setMaterial(Material.NAME_TAG);
        itemBuilder.setDisplayName(element.getName());
        return itemBuilder.build();
    }

    @Override
    protected CustomItemBuilder createSlotBase(){
        CustomItemBuilder itemBuilder = new CustomItemBuilder();
        itemBuilder.setMaterial(Material.NAME_TAG);
        return itemBuilder;
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+"Name";
    }

    @Override
    protected List<String> getValueDisplay() {
        return Arrays.asList(this.element.getName());
    }

    @Override
    protected List<String> getNormalDescription() {
        return Arrays.asList("Benanntes Namensschild","einsetzen");
    }

    @Override
    protected boolean isComplete() {
        return true;
    }

}
