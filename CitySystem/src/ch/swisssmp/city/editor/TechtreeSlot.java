package ch.swisssmp.city.editor;

import ch.swisssmp.city.Techtree;
import ch.swisssmp.city.TechtreeView;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ButtonSlot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TechtreeSlot extends ButtonSlot {

    private final Techtree techtree;

    public TechtreeSlot(CustomEditorView view, int slot, Techtree techtree) {
        super(view, slot);
        this.techtree = techtree;
    }

    @Override
    protected void triggerOnClick(ClickType clickType) {
        this.getView().closeLater(()->{
            TechtreeView.open(this.getView().getPlayer(), this.techtree);
        });
    }

    @Override
    protected boolean isComplete() {
        return true;
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+techtree.getName();
    }

    @Override
    protected List<String> getNormalDescription() {
        List<String> result = techtree.getDescription().stream().map(l -> ChatColor.GRAY + l).collect(Collectors.toList());
        result.add(0, ChatColor.GREEN+techtree.getId());
        return result;
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        return new CustomItemBuilder(Material.OAK_SAPLING);
    }
}
