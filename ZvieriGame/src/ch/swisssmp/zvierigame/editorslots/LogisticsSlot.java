package ch.swisssmp.zvierigame.editorslots;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.slot.PickItemSlot;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.zvierigame.ZvieriArenaEditor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogisticsSlot extends PickItemSlot {

    private final ZvieriArenaEditor view;

    public LogisticsSlot(ZvieriArenaEditor view, int slot) {
        super(view, slot);
        this.view = view;
    }

    @Override
    protected ItemStack createPick() {
        CustomItemBuilder itemBuilder = new CustomItemBuilder();
        itemBuilder.setMaterial(Material.LEAD);
        itemBuilder.setAmount(1);
        itemBuilder.setDisplayName(this.getName());
        List<String> description = new ArrayList<String>();
        description.add(this.getDescriptionColor() + "NPC für Logistiker");
        description.add(this.getSuggestActionColor() + "Rechtsklick auf NPC");
        itemBuilder.setLore(description);
        ItemStack itemStack = itemBuilder.build();
        ItemUtil.setString(itemStack, "link_zvieriarena", this.view.getArena().getId().toString());
        ItemUtil.setString(itemStack, "npc", "logistics");
        return itemStack;
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        CustomItemBuilder itemBuilder = new CustomItemBuilder();
        itemBuilder.setMaterial(Material.LEAD);
        return itemBuilder;
    }

    @Override
    public String getName() {
        return ChatColor.AQUA + "Logistiker";
    }

    @Override
    protected List<String> getNormalDescription() {
        return Arrays.asList("Logistiker zuweisen");
    }

    @Override
    protected boolean isComplete() {
        return true;
    }


}
