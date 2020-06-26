package ch.swisssmp.netherportals.configuration.slots;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.SelectSlot;
import ch.swisssmp.netherportals.CoordinateOperation;
import ch.swisssmp.netherportals.WorldConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class OperationSlot extends SelectSlot {

    private final WorldConfiguration configuration;

    public OperationSlot(CustomEditorView view, int slot, WorldConfiguration configuration) {
        super(view, slot);
        this.configuration = configuration;
    }

    @Override
    protected int getInitialValue() {
        return configuration.getOperation() != CoordinateOperation.DIVIDE ? 0 : 1;
    }

    @Override
    protected int getOptionsLength() {
        return 2;
    }

    @Override
    protected void onValueChanged(int value) {
        configuration.setOperation(value==0 ? CoordinateOperation.MULTIPLY : CoordinateOperation.DIVIDE);
        configuration.save();
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+"Verrechnung";
    }

    @Override
    protected List<String> getNormalDescription() {
        return Arrays.asList(
                ChatColor.GREEN+(configuration.getOperation()==CoordinateOperation.MULTIPLY ? "Multiplizieren" : "Dividieren"),
                ChatColor.GRAY+"Umrechnungsmethode für",
                ChatColor.GRAY+"Zielkoordinaten");
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        return new CustomItemBuilder(configuration.getOperation()==CoordinateOperation.MULTIPLY ? Material.NETHERRACK : Material.GRASS_BLOCK);
    }
}
