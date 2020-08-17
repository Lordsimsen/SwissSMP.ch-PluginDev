package ch.swisssmp.netherportals.configuration.slots;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.SelectSlot;
import ch.swisssmp.netherportals.WorldConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class TeleportWithoutPortalSlot extends SelectSlot {

    private final WorldConfiguration configuration;

    public TeleportWithoutPortalSlot(CustomEditorView view, int slot, WorldConfiguration configuration) {
        super(view, slot);
        this.configuration = configuration;
    }

    @Override
    protected int getInitialValue() {
        return configuration.getAllowTeleportWithoutPortal() ? 1 : 0;
    }

    @Override
    protected int getOptionsLength() {
        return 2;
    }

    @Override
    protected void onValueChanged(int value) {
        configuration.setAllowTeleportWithoutPortal(value==1);
        configuration.save();
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+"Teleport ohne Zielportal";
    }

    @Override
    protected List<String> getNormalDescription() {
        return Arrays.asList(
                (configuration.getAllowTeleportWithoutPortal() ? ChatColor.GREEN+"Zielportal nicht benötigt" : ChatColor.RED+"Zielportal benötigt"),
                ChatColor.GRAY+"Sollen Spieler auch ohne",
                ChatColor.GRAY+"Zielportal reisen können?"
        );
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        return new CustomItemBuilder(configuration.getAllowTeleportWithoutPortal() ? Material.HONEY_BLOCK : Material.HONEYCOMB_BLOCK);
    }
}
