package ch.swisssmp.warps;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class WarpsView extends CustomEditorView {

    protected WarpsView(Player player) {
        super(player);
    }

    @Override
    protected int getInventorySize() {
        return ((WarpPoints.getAll().size()+1) * 9)/9;
    }

    @Override
    protected Collection<EditorSlot> initializeEditor() {
        Collection<EditorSlot> result = new ArrayList<EditorSlot>();

        int i = 0;
        for(WarpPoint warp : WarpPoints.getAll()){
            result.add(new WarpSlot(this, warp, i));
            i++;
        }

        return result;
    }

    @Override
    public String getTitle() {
        return "Warps";
    }

    public static WarpsView open(Player player) {
        WarpsView warps = new WarpsView(player);
        warps.open();
        return warps;
    }
}
