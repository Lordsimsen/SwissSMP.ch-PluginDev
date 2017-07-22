package ch.swisssmp.knightstournament;

import java.util.ArrayList;
import java.util.List;

public class TournamentBracket {
	private final Tournament tournament;
	private final TournamentParticipant[] participants;
	private List<Duel> duels = new ArrayList<Duel>();
	public TournamentBracket(Tournament tournament, TournamentParticipant[] participants){
		this.tournament = tournament;
		this.participants = participants;
	}
	
	private void generateDuels(){
		TournamentParticipant playerOne = null;
		int remainingCount = 0;
		int outCount = 0;
		for(int i = 0; i < participants.length; i++){
			if(participants[i].isOut())
				outCount++;
			else
				remainingCount++;
		}
		if(remainingCount==1) return;
		String duelName = "Duell";
		if(remainingCount==2 && outCount>=4) duelName = "Finale";
		else if(remainingCount==4 && outCount>=4) duelName = "Halbfinale";
		else if(remainingCount==8 && outCount>=8) duelName = "Viertelfinale";
		for(int i = 0; i < participants.length; i++){
			if(participants[i].isOut()) continue;
			if(playerOne==null){
				playerOne = participants[i];
				continue;
			}
			duels.add(new Duel(this.tournament, duelName, playerOne, participants[i]));
			playerOne = null;
		}
	}
	
	public Duel getNextDuel(){
		if(this.duels.size()<1){
			this.generateDuels();
		}
		if(this.duels.size()<1){
			return null;
		}
		Duel duel = duels.get(0);
		duels.remove(0);
		this.tournament.runningDuel = duel;
		return duel;
	}
}
