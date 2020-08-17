package ch.swisssmp.city.editor;

import ch.swisssmp.city.CityLevel;
import ch.swisssmp.city.LevelState;
import ch.swisssmp.city.LevelStateInfo;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.InfoSlot;

import java.util.ArrayList;
import java.util.List;

public class CityLevelSlot extends InfoSlot {

    private final CityLevel level;
    private final LevelStateInfo stateInfo;

    public CityLevelSlot(CustomEditorView view, int slot, CityLevel level, LevelStateInfo stateInfo) {
        super(view, slot);
        this.level = level;
        this.stateInfo = stateInfo;
    }

    @Override
    protected boolean isComplete() {
        return true;
    }

    @Override
    public String getName() {
        return level.getColor()+level.getName();
    }

    @Override
    protected List<String> getNormalDescription() {
        if(stateInfo ==null) return null;
        LevelState state = stateInfo.getState();
        if(state==LevelState.UNLOCKED) return null;
        List<String> result = new ArrayList<>();
        result.add(state.getColor()+state.getDisplayName());
        if(stateInfo.getMessage()==null) return result;
        result.add("");
        result.addAll(stateInfo.getMessage());
        return result;
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        return level.getTokenBuilder(stateInfo!=null ? stateInfo.getState() : null);
    }
}
