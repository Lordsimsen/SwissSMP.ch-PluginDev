package ch.swisssmp.camerastudio.editor.sequence;

import ch.swisssmp.camerastudio.CameraPathElement;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ValueSlot;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class PathElementSlot extends ValueSlot {

    private CameraPathElement element;
    private Consumer<CameraPathElement> onChangedCallback;

    public PathElementSlot(CustomEditorView view, int slot, CameraPathElement element) {
        super(view, slot);
        this.element = element;
    }

    public PathElementSlot onChanged(Consumer<CameraPathElement> callback){
        this.onChangedCallback = callback;
        return this;
    }

    public CameraPathElement getElement(){
        return element;
    }

    @Override
    protected ItemStack createPick() {
        ItemStack result = element!=null ? element.getItemStack() : null;
        if(element==null) return result;
        this.element = null;
        if(onChangedCallback!=null){
            try{
                onChangedCallback.accept(null);
            } catch(Exception e){
                e.printStackTrace();
            }

        }
        return result;
    }

    @Override
    protected boolean applyValue(ItemStack itemStack) {
        CameraPathElement element = CameraPathElement.find(itemStack).orElse(null);
        if(element==null) return false;
        this.element = element;
        this.setItem(this.createSlot());
        try{
            if(this.onChangedCallback!=null) this.onChangedCallback.accept(element);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected boolean isComplete() {
        return element!=null;
    }

    @Override
    public String getName() {
        return element!=null ? element.getName() : "";
    }

    @Override
    protected List<String> getNormalDescription() {
        return null;
    }

    @Override
    protected ItemStack createSlot(){
        return element!=null ? element.getItemStack() : null;
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        return null; // not needed
    }
}
