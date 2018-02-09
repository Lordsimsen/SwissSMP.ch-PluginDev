package ch.swisssmp.knightstournament;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.SwissSMPler;

public class TournamentParticipant {
	private UUID player_uuid;
	private final List<TournamentParticipant> wonAgainst = new ArrayList<TournamentParticipant>();
	private TournamentParticipant lostAgainst = null;
	public TournamentParticipant(Player player){
		if(player!=null)
			this.player_uuid = player.getUniqueId();
		else
			this.player_uuid = null;
	}
	public UUID getPlayerUUID(){
		return this.player_uuid;
	}
	public void setPlayerUUID(UUID player_uuid){
		this.player_uuid = player_uuid;
	}
	public Horse getHorse(){
		Player player = Bukkit.getPlayer(this.player_uuid);
		if(player==null) return null;
		Entity vehicle = player.getVehicle();
		if(vehicle==null) return null;
		else if(!(vehicle instanceof Horse)) return null;
		else return (Horse) vehicle;
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
	public TournamentParticipant getLostAgainst(){
		return this.lostAgainst;
	}
	public boolean isOut(){
		return this.lostAgainst!=null;
	}
	public void sendMessage(String message){
		Player player = Bukkit.getPlayer(this.player_uuid);
		if(player!=null) player.sendMessage(message);
	}
	public void sendTitle(String title, String subtitle){
		Player player = Bukkit.getPlayer(this.player_uuid);
		if(player!=null) SwissSMPler.get(player).sendTitle(title, subtitle);
	}
	public void sendActionBar(String message){
		Player player = Bukkit.getPlayer(this.player_uuid);
		if(player!=null) SwissSMPler.get(player).sendActionBar(message);
	}
}
