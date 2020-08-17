package ch.swisssmp.zones.editor.slots;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ButtonSlot;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zones.editor.ZoneEditor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class CancelZoneSelectionSlot extends ButtonSlot {

    private final ZoneEditor editor;

    private boolean confirmed = false;

    public CancelZoneSelectionSlot(CustomEditorView view, int slot, ZoneEditor editor) {
        super(view, slot);
        this.editor = editor;
    }

    @Override
    protected void triggerOnClick(ClickType arg0) {
        if(!confirmed){
            confirmed = true;
            this.setItem(this.createSlot());
            return;
        }
        this.getView().closeLater();
        editor.cancel();
        SwissSMPler.get(this.getView().getPlayer()).sendActionBar(ChatColor.GRAY+"Auswahl abgebrochen");
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
            return ChatColor.RED+"Auswahl abbrechen";
        }
        else{
            return ChatColor.RED+"Auswahl wirklich abbrechen?";
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
