package ch.swisssmp.netherportals.configuration.slots;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ButtonSlot;
import ch.swisssmp.netherportals.WorldConfiguration;
import ch.swisssmp.netherportals.configuration.NetherPortalConfigurationView;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.List;

public class DefaultsSlot extends ButtonSlot {

    private final WorldConfiguration configuration;

    public DefaultsSlot(CustomEditorView view, int slot, WorldConfiguration configuration) {
        super(view, slot);
        this.configuration = configuration;
    }

    @Override
    protected void triggerOnClick(ClickType clickType) {
        configuration.applyDefaults();
        configuration.save();
        this.getView().closeLater(()->NetherPortalConfigurationView.open(getView().getPlayer(), configuration));
    }

    @Override
    protected boolean isComplete() {
        return true;
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+"Einstellungen zurücksetzen";
    }

    @Override
    protected List<String> getNormalDescription() {
        return Arrays.asList(ChatColor.GRAY+"Setzt alle Einstellungen",ChatColor.GRAY+"auf ihre Standardwerte", ChatColor.GRAY+"zurück.");
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        return new CustomItemBuilder(Material.PAPER);
    }
}
