package ch.swisssmp.zvieriplausch.editorslots;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.slot.PickItemSlot;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.zvieriplausch.ZvieriArenaEditor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StorageChestSlot extends PickItemSlot {

    private final ZvieriArenaEditor view;

    public StorageChestSlot(ZvieriArenaEditor view, int slot) {
        super(view, slot);
        this.view = view;
    }

    @Override
    protected ItemStack createPick() {
        CustomItemBuilder itemBuilder = new CustomItemBuilder();
        itemBuilder.setMaterial(Material.CHEST);
        itemBuilder.setAmount(1);
        itemBuilder.setDisplayName(this.getName());
        List<String> description = new ArrayList<String>();
        description.add(this.getDescriptionColor() + "Lagerkiste zuweisen");
        description.add(this.getSuggestActionColor() + "Rechtsklick auf Kiste");
        itemBuilder.setLore(description);
        ItemStack itemStack = itemBuilder.build();
        ItemUtil.setString(itemStack, "link_zvieriarena", this.view.getArena().getId().toString());
        ItemUtil.setString(itemStack, "zvieritool", "chest");
        return itemStack;
    }

    @Override
    protected boolean isComplete() {
        return false;
    }

    @Override
    public String getName() {
        return ChatColor.AQUA + "Lagerkiste";
    }

    @Override
    protected List<String> getNormalDescription() {
        return Arrays.asList("Lagerkiste zuweisen");
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        CustomItemBuilder itemBuilder = new CustomItemBuilder();
        itemBuilder.setMaterial(Material.CHEST);
        return itemBuilder;
    }
}
