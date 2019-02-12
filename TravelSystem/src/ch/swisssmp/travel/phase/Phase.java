package ch.swisssmp.travel.phase;

import org.bukkit.event.player.PlayerRespawnEvent;

import ch.swisssmp.travel.Journey;

public abstract class Phase implements Runnable {

	private final Journey journey;
	private boolean completed = false;
	
	protected Phase(Journey journey){
		this.journey = journey;
	}
	
	protected Journey getJourney(){
		return journey;
	}
	
	public void setCompleted(){
		completed = true;
	}
	
	public boolean isCompleted(){
		return completed;
	}

	public abstract void initialize();
	public abstract void finish();
	public abstract void complete();
	public abstract void cancel();
	
	public abstract void onPlayerRespawn(PlayerRespawnEvent event);
}
