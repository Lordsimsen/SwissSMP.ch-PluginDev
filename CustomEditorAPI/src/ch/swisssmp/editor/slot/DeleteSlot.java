package ch.swisssmp.editor.slot;

import java.util.List;

import ch.swisssmp.editor.Removable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.utils.SwissSMPler;

public class DeleteSlot extends ButtonSlot {

    private final Removable removable;
    private final String objectLabel;
    private Runnable onRemovedCallback;

    private boolean confirmed = false;

    public DeleteSlot(CustomEditorView view, int slot, Removable removable, String objectLabel) {
        super(view, slot);
        this.removable = removable;
        this.objectLabel = objectLabel;
    }

    public DeleteSlot onRemove(Runnable callback){
        this.onRemovedCallback = callback;
        return this;
    }

    @Override
    protected void triggerOnClick(ClickType arg0) {
        if(!confirmed){
            confirmed = true;
            this.setItem(this.createSlot());
            return;
        }
        this.getView().closeLater();
        removable.remove();
        SwissSMPler.get(this.getView().getPlayer()).sendActionBar(ChatColor.RED+objectLabel+" entfernt");
        if(onRemovedCallback!=null) onRemovedCallback.run();
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        if(!this.confirmed){
            CustomItemBuilder result = new CustomItemBuilder();
            result.setMaterial(Material.BARRIER);
            return result;
        }
        return CustomItems.getCustomItemBuilder("CHECKMARK");
    }

    @Override
    public String getName() {
        if(!confirmed){
            return ChatColor.RED+objectLabel+" entfernen";
        }
        else{
            return ChatColor.RED+objectLabel+" wirklich entfernen?";
        }
    }

    @Override
    protected List<String> getNormalDescription() {
        return null;
    }

    @Override
    protected boolean isComplete() {
        return true;
    }
}
