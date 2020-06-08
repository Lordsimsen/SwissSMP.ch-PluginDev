package ch.swisssmp.camerastudio.editor.sequence;

import ch.swisssmp.camerastudio.CameraPath;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ValueRangeSlot;
import ch.swisssmp.utils.Mathf;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class TimeSlot extends ValueRangeSlot {

    private final PathElementSlot elementSlot;
    private int value;
    private ItemStack itemStack;

    public TimeSlot(CustomEditorView view, int slot, PathElementSlot elementSlot, int value) {
        super(view, slot);
        this.elementSlot = elementSlot;
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public void show(){
        this.setItemLater(this.createSlot());
    }

    public void hide(){
        this.setItemLater(null);
    }

    @Override
    protected float getInitialValue() {
        return value;
    }

    @Override
    protected float getMinValue() {
        return 0;
    }

    @Override
    protected float getMaxValue() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected float getStep() {
        return 1;
    }

    @Override
    protected float getBigStep() {
        return Math.max(1, Mathf.ceilToInt(value / 20f)) * 5;
    }

    @Override
    protected void onValueChanged(float value) {
        this.value = Mathf.roundToInt(value);
        if(itemStack!=null){
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(this.getName());
            itemStack.setItemMeta(itemMeta);
            this.setItem(itemStack);
        }
    }

    @Override
    protected boolean isComplete() {
        return value>0;
    }

    @Override
    public String getName() {
        return ChatColor.WHITE+String.valueOf(value)+"t";
    }

    @Override
    protected List<String> getNormalDescription() {
        return Arrays.asList(ChatColor.GRAY+"Länge in Ticks",ChatColor.GRAY+"20 ticks = 1s");
    }

    @Override
    protected ItemStack createSlot(){
        if(!(elementSlot.getElement() instanceof CameraPath)){
            itemStack = null;
            return itemStack;
        }
        CustomItemBuilder itemBuilder = new CustomItemBuilder();
        itemBuilder.setMaterial(Material.CLOCK);
        itemBuilder.setAmount(1);
        itemBuilder.setDisplayName(this.getName());
        itemBuilder.setLore(this.getNormalDescription());
        itemStack = itemBuilder.build();
        return itemStack;
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        return null; // not needed
    }
}
