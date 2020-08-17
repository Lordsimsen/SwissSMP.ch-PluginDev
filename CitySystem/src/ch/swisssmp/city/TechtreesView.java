package ch.swisssmp.city;

import ch.swisssmp.city.editor.TechtreeSlot;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.utils.Mathf;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TechtreesView extends CustomEditorView {

    private final List<Techtree> techtrees;

    protected TechtreesView(Player player, List<Techtree> techtrees) {
        super(player);
        this.techtrees = techtrees;
    }

    @Override
    protected int getInventorySize() {
        return Math.max(1, Mathf.ceilToInt(techtrees.size()/9f))*9;
    }

    @Override
    protected Collection<EditorSlot> initializeEditor() {
        List<EditorSlot> slots = new ArrayList<>();
        for(int i = 0; i < techtrees.size(); i++){
            slots.add(new TechtreeSlot(this, i, techtrees.get(i)));
        }
        return slots;
    }

    @Override
    public String getTitle() {
        return "Techtrees";
    }

    public static TechtreesView open(Player player){
        List<Techtree> techtrees = CitySystem.getTechtrees().stream().sorted((a,b)->a.getName().compareTo(b.getName())).collect(Collectors.toList());
        TechtreesView result = new TechtreesView(player, techtrees);
        result.open();
        return result;
    }
}
