package ch.swisssmp.citymapdisplays.editor;

import ch.swisssmp.citymapdisplays.CityMapDisplay;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.custompaintings.CustomPainting;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.PickItemSlot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CityMapDisplaySlot extends PickItemSlot {

    private final CityMapDisplay display;
    private final int width;
    private final int height;

    public CityMapDisplaySlot(CustomEditorView view, int slot, CityMapDisplay display) {
        super(view, slot);
        this.display = display;

        CustomPainting painting = display.getPainting();
        if(painting==null){
            width = 0;
            height = 0;
        }
        else{
            width = painting.getWidth();
            height = painting.getHeight();
        }
    }

    @Override
    protected ItemStack createPick() {
        return display.getItemStack();
    }

    @Override
    protected boolean isComplete() {
        return true;
    }

    @Override
    public String getName() {
        return ChatColor.RESET+display.getName();
    }

    @Override
    protected List<String> getNormalDescription() {
        return Collections.singletonList("Grösse: " + width + "x" + height);
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        CustomItemBuilder result = new CustomItemBuilder();
        result.setMaterial(Material.WRITTEN_BOOK);
        result.setAmount(1);
        return result;
    }
}
