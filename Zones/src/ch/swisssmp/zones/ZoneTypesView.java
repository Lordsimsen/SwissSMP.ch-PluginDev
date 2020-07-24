package ch.swisssmp.zones;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.PaginatedView;
import ch.swisssmp.editor.slot.ChangePageSlot;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.utils.Mathf;
import ch.swisssmp.zones.editor.slots.ZoneTypeSlot;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ZoneTypesView extends CustomEditorView implements PaginatedView {

    private static final int COLUMNS = 8;

    private final Collection<ZoneType> zoneTypes;

    private final int pageSize;
    private final int maxPage;

    private int page = 0;

    protected ZoneTypesView(Player player, Collection<ZoneType> zoneTypes) {
        super(player);
        this.zoneTypes = zoneTypes;
        this.pageSize = Math.max(18, Math.min(54, Mathf.ceilToInt(zoneTypes.size()/(float) COLUMNS)*9));
        this.maxPage = Math.max(0, Mathf.ceilToInt(zoneTypes.size() / (double) pageSize) - 1);
    }

    @Override
    protected int getInventorySize() {
        return pageSize;
    }

    @Override
    protected Collection<EditorSlot> initializeEditor() {
        int index = 0;
        Collection<EditorSlot> slots = new ArrayList<>();
        if(page>0) slots.add(new ChangePageSlot(this, 8, false));
        if(page<maxPage) slots.add(new ChangePageSlot(this, pageSize-1, true));
        for(ZoneType type : zoneTypes){
            if(type.isInternal()) continue;
            int slot = remapSlot(index);
            slots.add(new ZoneTypeSlot(this, slot, type));
            index++;
        }
        return slots;
    }

    @Override
    public String getTitle() {
        return "Zonen-Typen";
    }

    public void pageUp() {
        if(page<=0) return;
        page--;
        this.recreateSlots();
    }

    public void pageDown() {
        if(page>=maxPage) return;
        page++;
        this.recreateSlots();
    }

    public void setPage(int page){
        this.page = page;
        this.recreateSlots();
    }

    private int remapSlot(int index){
        int row = Mathf.floorToInt(index / (float) COLUMNS);
        int column = index - (row * COLUMNS);
        return row * 9 + column;
    }

    public static ZoneTypesView open(Player player){
        Collection<ZoneType> types = ZoneTypes.getAll().stream().sorted(Comparator.comparing(ZoneType::getName)).collect(Collectors.toList());
        ZoneTypesView view = new ZoneTypesView(player, types);
        view.open();
        return view;
    }
}
