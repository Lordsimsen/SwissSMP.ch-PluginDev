package ch.swisssmp.knightstournament;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.SwissSMPler;

public class TournamentParticipant {
	private final Player player;
	private final Horse horse;
	private final List<TournamentParticipant> wonAgainst = new ArrayList<TournamentParticipant>();
	private TournamentParticipant lostAgainst = null;
	public TournamentParticipant(Player player, Horse horse){
		this.player = player;
		this.horse = horse;
	}
	public Player getPlayer(){
		return this.player;
	}
	public Horse getHorse(){
		return this.horse;
	}
	public void addWonAgainst(TournamentParticipant loser){
		this.wonAgainst.add(loser);
	}
	public TournamentParticipant[] getWonAgainst(){
		return wonAgainst.toArray(new TournamentParticipant[this.wonAgainst.size()]);
	}
	public void setLostAgainst(TournamentParticipant winner){
		this.lostAgainst = winner;
	}
	public boolean isOut(){
		return this.lostAgainst!=null;
	}
	public void sendMessage(String message){
		this.player.sendMessage(message);
	}
	public void sendTitle(String title, String subtitle){
		if(this.player==null) return;
		SwissSMPler.get(this.player).sendTitle(title, subtitle);
	}
	public void sendActionbar(String message){
		if(this.player==null) return;
		SwissSMPler.get(this.player).sendActionBar(message);
	}
}
