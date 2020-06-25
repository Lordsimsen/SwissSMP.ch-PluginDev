package ch.swisssmp.travel.phase;

import org.bukkit.event.player.PlayerRespawnEvent;

import ch.swisssmp.travel.Journey;

public abstract class Phase implements Runnable {

	private final Journey journey;
	private boolean completed = false;
	private boolean cancelled = false;
	private boolean finished = false;
	
	protected Phase(Journey journey){
		this.journey = journey;
	}
	
	protected Journey getJourney(){
		return journey;
	}
	
	public void setCompleted(){
		completed = true;
	}
	public void setCancelled(){
		cancelled = true;
	}
	
	public boolean isCompleted(){
		return completed;
	}
	public boolean isCancelled(){
		return cancelled;
	}
	public boolean isFinished(){
		return finished;
	}

	public abstract void initialize();
	public abstract void complete();
	public abstract void cancel();
	public void finish(){
		finished = true;
	}
	
	public abstract void onPlayerRespawn(PlayerRespawnEvent event);
}
