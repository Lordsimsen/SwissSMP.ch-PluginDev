package ch.swisssmp.travel.phase;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;

import ch.swisssmp.travel.Journey;
import ch.swisssmp.travel.TravelStation;
import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.SwissSMPler;

public class PreparationPhase extends Phase {

	private long time = 0L; //how far the phase has progressed
	private final long duration; //how long the phase lasts
	
	private int lastRemaining = -1;
	
	public PreparationPhase(Journey journey, long duration){
		super(journey);
		this.duration = duration;
	}
	
	@Override
	public void run() {
		time++;
		int remaining = Mathf.ceilToInt((duration-time)/20);
		if(remaining!=lastRemaining){
			for(Player player : this.getJourney().getPlayers()){
				this.sendCountdown(player, remaining);
			}
			lastRemaining = remaining;
		}
		if(time>=duration){
			setCompleted();
		}
	}
	
	public void reset(){
		this.time = 0;
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}
	
	private void sendCountdown(Player player, int remaining){
		if(remaining>0) SwissSMPler.get(player).sendTitle("", "Abreise in "+remaining);
		else SwissSMPler.get(player).sendTitle("", "Los gehts!");
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finish() {
		super.finish();
		for(Player player : this.getJourney().getPlayers()){
			SwissSMPler.get(player).sendTitle("", "");
		}
	}

	@Override
	public void complete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		TravelStation start = this.getJourney().getStart();
		Location location = start.getWaypoint().getLocation(start.getWorld());
		event.setRespawnLocation(location);
	}
}
