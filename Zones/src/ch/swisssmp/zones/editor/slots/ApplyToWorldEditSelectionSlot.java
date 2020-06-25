package ch.swisssmp.zones.editor.slots;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ButtonSlot;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zones.WorldEditHandler;
import ch.swisssmp.zones.Zone;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.List;

public class ApplyToWorldEditSelectionSlot extends ButtonSlot {

    private final Zone zone;

    public ApplyToWorldEditSelectionSlot(CustomEditorView view, int slot, Zone zone) {
        super(view, slot);
        this.zone = zone;
    }

    @Override
    protected void triggerOnClick(ClickType clickType) {
        if(!zone.isSetupComplete()) return;
        Player player = getView().getPlayer();
        WorldEditHandler.select(player, zone);
        this.getView().closeLater();
        SwissSMPler.get(player).sendActionBar(ChatColor.GREEN+"Zone ausgewählt.");
    }

    @Override
    protected boolean isComplete() {
        return zone.isSetupComplete();
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+"Zone mit WorldEdit auswählen";
    }

    @Override
    protected List<String> getNormalDescription() {
        if(!zone.isSetupComplete()){
            return Arrays.asList(ChatColor.YELLOW+"Zuerst Zone fertig",ChatColor.YELLOW+"aufsetzen");
        }
        return Arrays.asList(ChatColor.GRAY+"Wählt den Zonen-Bereich",ChatColor.GRAY+"mit WorldEdit aus");
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        return new CustomItemBuilder(Material.WOODEN_AXE);
    }
}
