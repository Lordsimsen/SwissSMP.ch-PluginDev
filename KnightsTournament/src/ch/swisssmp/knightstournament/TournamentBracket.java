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
		List<TournamentParticipant> remainingList = new ArrayList<TournamentParticipant>();
		for(int i = 0; i < participants.length; i++){
			if(participants[i].isOut()) continue;
			remainingList.add(participants[i]);
		}
		TournamentParticipant[] remaining = remainingList.toArray(new TournamentParticipant[remainingList.size()]);
		if(remaining.length<2) return;
		String duelName = "Duell";
		if(remaining.length==2 && participants.length>4) duelName = "Finale";
		else if(remaining.length==4 && participants.length>4) duelName = "Halbfinale";
		else if(remaining.length==8 && participants.length>8) duelName = "Viertelfinale";
		int half = remaining.length/2;
		TournamentParticipant playerOne;
		TournamentParticipant playerTwo;
		for(int i = 0; i < remaining.length/2; i++){
			playerOne = remaining[i];
			playerTwo = remaining[i+half];
			duels.add(new Duel(this.tournament, duelName, playerOne, playerTwo));
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
