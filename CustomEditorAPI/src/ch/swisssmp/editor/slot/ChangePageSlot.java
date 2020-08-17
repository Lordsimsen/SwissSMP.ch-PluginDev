package ch.swisssmp.editor.slot;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.PaginatedView;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.List;

public class ChangePageSlot extends ButtonSlot {

    private final PaginatedView view;
    private final boolean pageDown;
    private final String label;

    public ChangePageSlot(PaginatedView view, int slot, boolean pageDown) {
        this(view, slot, pageDown, null);
    }

    public ChangePageSlot(PaginatedView view, int slot, boolean pageDown, String label) {
        super((CustomEditorView) view, slot);
        this.view = view;
        this.pageDown = pageDown;
        this.label = label;
    }

    @Override
    protected void triggerOnClick(ClickType clickType) {
        if(pageDown) {
            view.pageDown();
        }
        else {
            view.pageUp();
        }
    }

    @Override
    protected boolean isComplete() {
        return true;
    }

    @Override
    public String getName() {
        return label!=null ? label : pageDown ? "Nächste Seite" : "Vorherige Seite";
    }

    @Override
    protected List<String> getNormalDescription() {
        return Arrays.asList();
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        CustomItemBuilder result = new CustomItemBuilder();
        result.setMaterial(Material.PAPER);
        result.setAmount(1);
        return result;
    }
}
