package ch.swisssmp.fortressassault;

public enum GameState {
PREGAME,
BUILD,
FIGHT,
FINISHED;
public GameState next(){
	switch(this){
	case PREGAME:
		return BUILD;
	case BUILD:
		return FIGHT;
	case FINISHED:
		return FINISHED;
	default:
		return null;
	}
}
}
