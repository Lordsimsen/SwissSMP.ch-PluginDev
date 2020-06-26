package ch.swisssmp.netherportals.configuration.slots;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ValueSlot;
import ch.swisssmp.netherportals.PortalLinkCache;
import ch.swisssmp.netherportals.WorldConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class TargetWorldSlot extends ValueSlot {

    private final WorldConfiguration configuration;

    public TargetWorldSlot(CustomEditorView view, int slot, WorldConfiguration configuration) {
        super(view, slot);
        this.configuration = configuration;
    }

    @Override
    protected ItemStack createPick() {
        return new CustomItemBuilder(Material.NAME_TAG).setDisplayName(configuration.getTargetWorld()).build();
    }

    @Override
    protected boolean applyValue(ItemStack itemStack) {
        String displayName = (itemStack!=null && itemStack.hasItemMeta() ? itemStack.getItemMeta().getDisplayName() : null);
        configuration.setTargetWorld(displayName);
        configuration.save();
        PortalLinkCache.invalidate(configuration.getBukkitWorld());
        return true;
    }

    @Override
    protected boolean isComplete() {
        return configuration.getTargetWorld()!=null;
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+"Zielwelt";
    }

    @Override
    protected List<String> getNormalDescription() {
        return Arrays.asList(
                (configuration.getTargetWorld()!=null ? ChatColor.GREEN+configuration.getTargetWorld() : ChatColor.YELLOW+"Vanilla-Portale belassen"),
                ChatColor.GRAY+"Benanntes Namensschild",
                ChatColor.GRAY+"einsetzen",
                ChatColor.GRAY+">> Unbenanntes einsetzen",
                ChatColor.GRAY+"um vanilla Verhalten",
                ChatColor.GRAY+"zu belassen");
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        return new CustomItemBuilder(Material.MAP);
    }
}
