package ch.swisssmp.zones;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.PaginatedView;
import ch.swisssmp.editor.slot.ChangePageSlot;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.utils.Mathf;
import ch.swisssmp.zones.editor.slots.ZoneSlot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class ZonesView extends CustomEditorView implements PaginatedView {

    private static final int COLUMNS = 8;

    private final Collection<Zone> zones;
    private final ZoneType type;

    private final int pageSize;
    private final int maxPage;

    private int page = 0;

    protected ZonesView(Player player, ZoneType type, Collection<Zone> zones) {
        super(player);
        this.zones = zones;
        this.type = type;
        this.pageSize = Math.max(18, Math.min(54, Mathf.ceilToInt(zones.size()/(float) COLUMNS)*9));
        this.maxPage = Math.max(0, Mathf.ceilToInt(zones.size() / (double) pageSize) - 1);
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
        for(Zone zone : zones){
            int slot = remapSlot(index);
            slots.add(new ZoneSlot(this, slot, zone));
            index++;
        }
        return slots;
    }

    @Override
    public String getTitle() {
        return "Zonen ("+type.getName()+ ChatColor.RESET+")";
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

    public static CustomEditorView open(Player player, ZoneType zoneType){
        ZoneCollection collection = ZoneContainer.get(player.getWorld()).getCollection(zoneType).orElse(null);
        if(collection==null){
            return ZoneTypesView.open(player);
        }
        Collection<Zone> displays = collection.getAllZones();
        ZonesView view = new ZonesView(player, zoneType, displays);
        view.open();
        return view;
    }
}
