package ch.swisssmp.customportals.editor;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customportals.CustomPortal;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.SelectSlot;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class KeepVelocitySlot extends SelectSlot {

    private final CustomPortal portal;

    public KeepVelocitySlot(CustomEditorView view, int slot, CustomPortal portal) {
        super(view, slot);
        this.portal = portal;
    }

    @Override
    protected int getInitialValue() {
        return portal.getKeepVelocity() ? 1 : 0;
    }

    @Override
    protected int getOptionsLength() {
        return 2;
    }

    @Override
    protected void onValueChanged(int arg0) {
        this.portal.setKeepVelocity(arg0==1);
        this.portal.getContainer().save();
        this.portal.updateTokens();
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        CustomItemBuilder result = new CustomItemBuilder();
        result.setMaterial(this.getValue()==1 ? Material.ELYTRA : Material.ANVIL);
        return result;
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+"Geschwindigkeit beibehalten";
    }

    @Override
    protected List<String> getValueDisplay() {
        if(this.getValue()==1){
            return Arrays.asList("Bewegung bleibt");
        }
        else{
            return Arrays.asList("Geschwindigkeit wird", "zurückgesetzt");
        }
    }

    @Override
    protected List<String> getNormalDescription() {
        return null;
    }
}
