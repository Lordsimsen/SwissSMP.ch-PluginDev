package ch.swisssmp.city;

import java.util.List;

public class LevelStateInfo {
    private final LevelState state;
    private final List<String> message;

    public LevelStateInfo(LevelState state){
        this.state = state;
        this.message = null;
    }

    public LevelStateInfo(LevelState state, List<String> message){
        this.state = state;
        this.message = message;
    }

    public LevelState getState(){return state;}
    public List<String> getMessage(){return message;}
}
