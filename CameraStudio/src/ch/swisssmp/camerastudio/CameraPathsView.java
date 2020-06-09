package ch.swisssmp.camerastudio;

import ch.swisssmp.camerastudio.editor.CameraPathSlot;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.PaginatedView;
import ch.swisssmp.editor.slot.ChangePageSlot;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.utils.Mathf;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

public class CameraPathsView extends CustomEditorView implements PaginatedView {

    private static final int COLUMNS = 8;

    private final Collection<CameraPath> paths;
    private final int pageSize;
    private final int maxPage;

    private int page = 0;

    protected CameraPathsView(Player player, Collection<CameraPath> paths) {
        super(player);
        this.paths = paths;
        this.pageSize = Math.max(18, Math.min(54, Mathf.ceilToInt(paths.size()/(float) COLUMNS)*9));
        this.maxPage = Math.max(0, Mathf.ceilToInt(paths.size() / (double) pageSize) - 1);
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
        for(CameraPath path : paths){
            int slot = remapSlot(index);
            slots.add(new CameraPathSlot(this, slot, path));
            index++;
        }
        return slots;
    }

    @Override
    public String getTitle() {
        return "Kamera-Pfade";
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

    public static CameraPathsView open(Player player){
        Collection<CameraPath> paths = CameraStudioWorld.get(player.getWorld()).getAllPaths().stream().sorted(Comparator.comparing(CameraPathElement::getName)).collect(Collectors.toList());
        CameraPathsView view = new CameraPathsView(player, paths);
        view.open();
        return view;
    }
}
