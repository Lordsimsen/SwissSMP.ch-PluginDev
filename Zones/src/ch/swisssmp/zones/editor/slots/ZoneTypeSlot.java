package ch.swisssmp.zones.editor.slots;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ButtonSlot;
import ch.swisssmp.zones.ZoneType;
import ch.swisssmp.zones.ZonesView;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.List;

public class ZoneTypeSlot extends ButtonSlot {

    private final ZoneType type;

    public ZoneTypeSlot(CustomEditorView view, int slot, ZoneType type) {
        super(view, slot);
        this.type = type;
    }

    @Override
    protected void triggerOnClick(ClickType clickType) {
        getView().closeLater(()-> ZonesView.open(getView().getPlayer(),type));
    }

    @Override
    protected boolean isComplete() {
        return true;
    }

    @Override
    public String getName() {
        return ChatColor.AQUA + type.getName();
    }

    @Override
    protected List<String> getNormalDescription() {
        return Arrays.asList(ChatColor.GRAY+"Klicke, um Zonen",ChatColor.GRAY+"dieses Types anzusehen");
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder(type.getCustomEnum());
        itemBuilder.setAmount(1);
        return itemBuilder;
    }
}
