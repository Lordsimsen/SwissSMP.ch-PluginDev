package ch.swisssmp.citymapdisplays;

import ch.swisssmp.citymapdisplays.editor.CityMapDisplaySlot;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.PaginatedView;
import ch.swisssmp.editor.slot.ChangePageSlot;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.utils.Mathf;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class CityMapDisplaysView extends CustomEditorView implements PaginatedView {

    private static final int COLUMNS = 8;

    private final Collection<CityMapDisplay> displays;
    private final int pageSize;
    private final int maxPage;

    private int page = 0;

    protected CityMapDisplaysView(Player player, Collection<CityMapDisplay> displays) {
        super(player);
        this.displays = displays;
        this.pageSize = Math.max(18, Math.min(54, Mathf.ceilToInt(displays.size()/(float) COLUMNS)*9));
        this.maxPage = Math.max(0, Mathf.ceilToInt(displays.size() / (double) pageSize) - 1);
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
        for(CityMapDisplay display : displays){
            int slot = remapSlot(index);
            slots.add(new CityMapDisplaySlot(this, slot, display));
            index++;
        }
        return slots;
    }

    @Override
    public String getTitle() {
        return "CityMapDisplays";
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

    public static CityMapDisplaysView open(Player player){
        Collection<CityMapDisplay> displays = CityMapDisplays.getAll();
        CityMapDisplaysView view = new CityMapDisplaysView(player, displays);
        view.open();
        return view;
    }
}
