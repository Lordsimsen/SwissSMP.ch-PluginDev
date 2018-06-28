package ch.swisssmp.towercontrol;

public enum GameState {
PREGAME,
FIGHT,
FINISHED;
public GameState next(){
	switch(this){
	case PREGAME:
		return FIGHT;
	case FIGHT:
		return FINISHED;
	default:
		return null;
	}
}
}
