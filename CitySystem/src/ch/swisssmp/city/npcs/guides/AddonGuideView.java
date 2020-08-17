package ch.swisssmp.city.npcs.guides;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.swisssmp.city.Addon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ch.swisssmp.city.npcs.guides.editor.AddonSlot;
import ch.swisssmp.city.npcs.guides.editor.AddonStateSlot;
import ch.swisssmp.city.npcs.guides.editor.RemoveGuideSlot;
import ch.swisssmp.city.npcs.guides.event.GuideViewEvent;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;

public class AddonGuideView extends CustomEditorView {

    private final AddonGuide guide;

    protected AddonGuideView(Player player, AddonGuide guide) {
        super(player);
        this.guide = guide;
    }

    @Override
    protected Collection<EditorSlot> initializeEditor() {
        Addon addon = guide.getAddon();
        List<EditorSlot> slots = new ArrayList<EditorSlot>();
        slots.add(new AddonSlot(this, 0, addon));
        slots.add(new AddonStateSlot(this, 1, addon));
        slots.add(new RemoveGuideSlot(this, 8, guide));
        return slots;
    }

    @Override
    public String getTitle() {
        return guide.getAddon().getName();
    }

    @Override
    protected int getInventorySize() {
        return 9;
    }

    public static AddonGuideView open(Player player, AddonGuide guide) {
        AddonGuideView result = new AddonGuideView(player, guide);
        GuideViewEvent event = new GuideViewEvent(result);
        Bukkit.getPluginManager().callEvent(event);
        result.open();
        return result;
    }
}
