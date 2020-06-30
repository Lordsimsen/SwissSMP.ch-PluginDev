package ch.swisssmp.netherportals.configuration.slots;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.SelectSlot;
import ch.swisssmp.netherportals.WorldConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class PortalCreationSlot extends SelectSlot {

    private final WorldConfiguration configuration;

    public PortalCreationSlot(CustomEditorView view, int slot, WorldConfiguration configuration) {
        super(view, slot);
        this.configuration = configuration;
    }

    @Override
    protected int getInitialValue() {
        return configuration.getAllowPortalCreation() ? 1 : 0;
    }

    @Override
    protected int getOptionsLength() {
        return 2;
    }

    @Override
    protected void onValueChanged(int value) {
        configuration.setAllowPortalCreation(value==1);
        configuration.save();
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+"Portalgenerierung";
    }

    @Override
    protected List<String> getNormalDescription() {
        return Arrays.asList(
                (configuration.getAllowPortalCreation() ? ChatColor.GREEN+"Portale werden generiert" : ChatColor.RED+"Keine Portale generieren "),
                ChatColor.GRAY+"Sollen in der Zielwelt",
                ChatColor.GRAY+"Portale generiert werden?"
        );
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        return new CustomItemBuilder(configuration.getAllowPortalCreation() ? Material.OBSIDIAN : Material.GLASS);
    }
}
