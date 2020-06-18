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

public class UseWorldEditSelectionSlot extends ButtonSlot {

    private final Zone zone;

    public UseWorldEditSelectionSlot(CustomEditorView view, int slot, Zone zone) {
        super(view, slot);
        this.zone = zone;
    }

    @Override
    protected void triggerOnClick(ClickType clickType) {
        this.getView().closeLater();
        Player player = getView().getPlayer();
        boolean success = WorldEditHandler.applySelection(player, zone);
        if(!success){
            SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Auswahl unvollständig.");
            return;
        }

        zone.save();
        SwissSMPler.get(player).sendActionBar(ChatColor.GREEN+"Zone angepasst!");
    }

    @Override
    protected boolean isComplete() {
        return true;
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+"WorldEdit-Auswahl anwenden";
    }

    @Override
    protected List<String> getNormalDescription() {
        return Arrays.asList(ChatColor.GRAY+"Passt die Zone aufgrund,",ChatColor.GRAY+"deiner Auswahl mit",ChatColor.GRAY+"WorldEdit an.");
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        CustomItemBuilder itemBuilder = new CustomItemBuilder();
        itemBuilder.setMaterial(Material.WOODEN_AXE);
        itemBuilder.setAmount(1);
        return itemBuilder;
    }
}
