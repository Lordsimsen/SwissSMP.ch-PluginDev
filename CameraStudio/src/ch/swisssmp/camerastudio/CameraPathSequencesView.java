package ch.swisssmp.camerastudio;

import ch.swisssmp.camerastudio.editor.CameraPathSequenceSlot;
import ch.swisssmp.camerastudio.editor.CameraPathSlot;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.PaginatedView;
import ch.swisssmp.editor.slot.ChangePageSlot;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.utils.Mathf;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class CameraPathSequencesView extends CustomEditorView implements PaginatedView {

    private static final int COLUMNS = 8;

    private final Collection<CameraPathSequence> sequences;
    private final int pageSize;
    private final int maxPage;

    private int page = 0;

    protected CameraPathSequencesView(Player player, Collection<CameraPathSequence> sequences) {
        super(player);
        this.sequences = sequences;
        this.pageSize = Math.max(18, Math.min(54, Mathf.ceilToInt(sequences.size()/(float) COLUMNS)*9));
        this.maxPage = Math.max(0, Mathf.ceilToInt(sequences.size() / (double) pageSize) - 1);
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
        for(CameraPathSequence sequence : sequences){
            int slot = remapSlot(index);
            slots.add(new CameraPathSequenceSlot(this, slot, sequence));
            index++;
        }
        return slots;
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

    @Override
    public String getTitle() {
        return "Kamera-Pfadsequenzen";
    }

    private int remapSlot(int index){
        int row = Mathf.floorToInt(index / (float) COLUMNS);
        int column = index - (row * COLUMNS);
        return row * 9 + column;
    }

    public static CameraPathSequencesView open(Player player){
        Collection<CameraPathSequence> sequences = CameraStudioWorlds.getAllSequences();
        CameraPathSequencesView view = new CameraPathSequencesView(player, sequences);
        view.open();
        return view;
    }
}
