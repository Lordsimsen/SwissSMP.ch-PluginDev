package ch.swisssmp.camerastudio;

import ch.swisssmp.camerastudio.editor.PathElementNameSlot;
import ch.swisssmp.camerastudio.editor.sequence.PathElementSlot;
import ch.swisssmp.camerastudio.editor.sequence.PlaySlot;
import ch.swisssmp.camerastudio.editor.sequence.TimeSlot;
import ch.swisssmp.camerastudio.editor.sequence.TourBookSlot;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.DeleteSlot;
import ch.swisssmp.editor.slot.EditorSlot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.*;

public class CameraPathSequenceEditor extends CustomEditorView {

    private static final int INVENTORY_SIZE = 27;

    private final CameraPathSequence sequence;

    protected CameraPathSequenceEditor(Player player, CameraPathSequence sequence) {
        super(player);
        this.sequence = sequence;
    }

    @Override
    protected int getInventorySize() {
        return INVENTORY_SIZE;
    }

    @Override
    protected Collection<EditorSlot> initializeEditor() {
        List<UUID> pathSequence = sequence.getPathSequence();
        List<Integer> timings = sequence.getTimings();
        Collection<EditorSlot> slots = new ArrayList<>();
        for(int i = 0; i < 8; i++){
            final int index = i;
            UUID elementUid = pathSequence.size()>index ? pathSequence.get(index) : null;
            CameraPathElement element = elementUid!=null ? CameraPathElement.find(elementUid).orElse(null) : null;
            PathElementSlot elementSlot = new PathElementSlot(this, index, element).onChanged((e)->onElementChanged(index,e));
            TimeSlot timeSlot = new TimeSlot(this, index+9, elementSlot, timings.size()>index ? timings.get(index) : 20);
            slots.add(elementSlot);
            slots.add(timeSlot);
        }
        slots.add(new PlaySlot(this, 8, sequence));
        slots.add(new TourBookSlot(this, 17, sequence));
        slots.add(new DeleteSlot(this, INVENTORY_SIZE-1, sequence, "Pfad-Sequenz").onRemove(()->sequence.getWorld().save()));
        return slots;
    }

    @Override
    public String getTitle() {
        return sequence.getName();
    }

    @Override
    protected void onInventoryClosed(InventoryCloseEvent event){
        savePathSequence();
    }

    public void savePathSequence(){
        List<UUID> pathSequence = new ArrayList<>();
        List<Integer> timings = new ArrayList<>();
        for(int i = 0; i < 8; i++){
            PathElementSlot elementSlot = (PathElementSlot) getSlot(i);
            TimeSlot timeSlot = (TimeSlot) getSlot(i+9);
            if(elementSlot.getElement()==null) continue;
            UUID elementUid = elementSlot.getElement().getUniqueId();
            pathSequence.add(elementUid);
            timings.add(timeSlot.getValue());
        }

        sequence.setPathSequence(pathSequence, timings);
        sequence.getWorld().save();
    }

    private void onElementChanged(int index, CameraPathElement element){
        TimeSlot slot = (TimeSlot) this.getSlot(index+9);
        if(!(element instanceof CameraPath)) slot.hide();
        else slot.show();
    }

    public static CameraPathSequenceEditor open(Player player, CameraPathSequence sequence){
        CameraPathSequenceEditor editor = new CameraPathSequenceEditor(player, sequence);
        editor.open();
        return editor;
    }
}
