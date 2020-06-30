package ch.swisssmp.netherportals.configuration.slots;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ValueRangeSlot;
import ch.swisssmp.netherportals.PortalLinkCache;
import ch.swisssmp.netherportals.WorldConfiguration;
import ch.swisssmp.utils.Mathf;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class TargetCoordinateFactorSlot extends ValueRangeSlot {

    private final WorldConfiguration configuration;

    public TargetCoordinateFactorSlot(CustomEditorView view, int slot, WorldConfiguration configuration) {
        super(view, slot);
        this.configuration = configuration;
    }

    @Override
    protected float getInitialValue() {
        return configuration.getTargetCoordinateFactor();
    }

    @Override
    protected float getMinValue() {
        return 1;
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
        return 16;
    }

    @Override
    protected void onValueChanged(float value) {
        int factor = Mathf.roundToInt(value);
        configuration.setTargetCoordinateFactor(factor);
        configuration.save();
        PortalLinkCache.invalidate(configuration.getBukkitWorld());
        this.setItem(this.createSlot());
    }

    @Override
    protected boolean isComplete() {
        return true;
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+"Umrechnungswert";
    }

    @Override
    protected List<String> getNormalDescription() {
        return Arrays.asList(
                ChatColor.GREEN+String.valueOf(configuration.getTargetCoordinateFactor()),
                ChatColor.GRAY+"Die Startkoordinaten",
                ChatColor.GRAY+"werden mit diesem",
                ChatColor.GRAY+"Wert verrechnet");
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        return new CustomItemBuilder(Material.REPEATER);
    }
}
