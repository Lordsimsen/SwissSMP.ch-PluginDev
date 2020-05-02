package ch.swisssmp.zvierigame.editorslots;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ButtonSlot;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zvierigame.ZvieriArena;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class CountersDeleteSlot extends ButtonSlot {

    private final ZvieriArena arena;

    private boolean confirmed = false;

    public CountersDeleteSlot(CustomEditorView view, int slot, ZvieriArena arena) {
        super(view, slot);
        this.arena = arena;
    }

    @Override
    protected void triggerOnClick(ClickType arg0) {
        if(!confirmed) {
            confirmed = true;
            this.setItem(this.createSlot());
            return;
        }
        this.getView().closeLater();
        arena.clearCounters();

        SwissSMPler.get((Player) this.getView().getPlayer()).sendActionBar(ChatColor.DARK_RED + "Theken-Markierungen entfernt");
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        if(!this.confirmed) {
            CustomItemBuilder result = new CustomItemBuilder();
            result.setMaterial(Material.BARRIER);
            return result;
        }
        return CustomItems.getCustomItemBuilder("CHECKMARK");
    }

    @Override
    public String getName() {
        if(!confirmed) {
            return ChatColor.RED + "Theken entfernen";
        } else {
            return ChatColor.RED + "Wirklich alle Theken entfernen?";
        }
    }

    @Override
    protected List<String> getNormalDescription() {
        return null;
    }

    @Override
    protected boolean isComplete() {
        return true;
    }
}
