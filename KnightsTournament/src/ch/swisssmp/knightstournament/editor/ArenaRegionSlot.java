package ch.swisssmp.knightstournament.editor;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ValueSlot;
import ch.swisssmp.knightstournament.KnightsArena;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ArenaRegionSlot extends ValueSlot {

    private final KnightsArena arena;

    public ArenaRegionSlot(CustomEditorView view, int slot, KnightsArena arena) {
        super(view, slot);
        this.arena = arena;
    }

    @Override
    protected ItemStack createPick() {
        ItemStack pick = new ItemStack(Material.NAME_TAG);
        ItemMeta itemMeta = pick.getItemMeta();
        itemMeta.setDisplayName(arena.getArenaRegion());
        pick.setItemMeta(itemMeta);
        return pick;
    }

    @Override
    protected boolean applyValue(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta==null || !itemMeta.hasDisplayName()) return false;
        arena.setArenaRegion(itemMeta.getDisplayName());
        KnightsArena.save(arena.getWorld());
        return true;
    }

    @Override
    protected boolean isComplete() {
        return arena.getArenaRegion()!=null;
    }

    @Override
    public String getName() {
        return "Arena-Zone";
    }

    @Override
    protected List<String> getNormalDescription() {
        return Arrays.asList("Zone, in welcher sich",
                "Teilnehmer frei bewegen können");
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        CustomItemBuilder result = new CustomItemBuilder();
        result.setMaterial(Material.MAP);
        return result;
    }
}
