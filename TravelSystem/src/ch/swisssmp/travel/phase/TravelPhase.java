package ch.swisssmp.travel.phase;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;

import ch.swisssmp.travel.Journey;
import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.SwissSMPler;

public class TravelPhase extends Phase {

	private final World worldInstance;
	
	private long time = 0L; //how far the phase has progressed
	private final long duration; //how long the phase lasts
	
	private int lastRemaining = -1;
	
	public TravelPhase(Journey journey, World worldInstance, long duration) {
		super(journey);
		this.worldInstance = worldInstance;
		this.duration = duration;
		if(this.worldInstance==null) this.setCompleted();
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
	
	private void sendCountdown(Player player, int remaining){
		if(remaining>0) SwissSMPler.get(player).sendActionBar("Ankunft in "+remaining);
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize() {
		for(Player player : this.getJourney().getPlayers()){
			player.setGameMode(GameMode.ADVENTURE);
		}
	}

	@Override
	public void finish() {
		for(Player player : this.getJourney().getPlayers()){
			player.setGameMode(GameMode.SURVIVAL);
		}
	}

	@Override
	public void complete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Location location = this.worldInstance.getSpawnLocation();
		event.setRespawnLocation(location);
	}
}
