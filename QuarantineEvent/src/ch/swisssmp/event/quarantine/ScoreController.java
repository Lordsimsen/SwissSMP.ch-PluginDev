package ch.swisssmp.event.quarantine;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class ScoreController {
	private HashMap<UUID,Integer> scores = new HashMap<UUID,Integer>();
	
	public void Initialize() {
		
	}
	
	public void reset() {
		scores.clear();
	}
	
	public void setPlayers(Collection<UUID> playerUids) {
		scores.clear();
		for(UUID uuid : playerUids) {
			scores.put(uuid, 0);
		}
	}
	
	public int getScore(UUID playerUid) {
		return scores.containsKey(playerUid) ? scores.get(playerUid) : 0;
	}
	
	public void addScore(UUID playerUid, int score) {
		int prevScore = getScore(playerUid);
		scores.put(playerUid, prevScore+score);
	}
	
	public void removeScore(UUID playerUid, int score) {
		int prevScore = getScore(playerUid);
		scores.put(playerUid, Math.max(0, prevScore-score));
	}
	
	public HashMap<UUID, Integer> getScores(){
		return scores;
	}
}
