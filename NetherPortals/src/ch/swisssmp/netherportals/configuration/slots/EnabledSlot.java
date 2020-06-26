package ch.swisssmp.netherportals.configuration.slots;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.SelectSlot;
import ch.swisssmp.netherportals.WorldConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class EnabledSlot extends SelectSlot {

    private final WorldConfiguration configuration;

    public EnabledSlot(CustomEditorView view, int slot, WorldConfiguration configuration) {
        super(view, slot);
        this.configuration = configuration;
    }

    @Override
    protected int getInitialValue() {
        return configuration.isEnabled() ? 1 : 0;
    }

    @Override
    protected int getOptionsLength() {
        return 2;
    }

    @Override
    protected void onValueChanged(int value) {
        configuration.setEnabled(value==1);
        configuration.save();
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+"Netherportale blockieren";
    }

    @Override
    protected List<String> getNormalDescription() {
        return Arrays.asList(
                (configuration.isEnabled() ? ChatColor.GREEN+"Netherportale sind aktiv" : ChatColor.RED+"Netherportale sind inaktiv"),
                ChatColor.GRAY+"Ermöglicht Blockierung",
                ChatColor.GRAY+"aller Netherportale",
                ChatColor.GRAY+"in dieser Welt");
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        return new CustomItemBuilder(configuration.isEnabled() ? Material.ENDER_EYE : Material.ENDER_PEARL);
    }
}
