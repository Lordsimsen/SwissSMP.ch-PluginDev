package ch.swisssmp.adventuredungeons.world;

import org.bukkit.Bukkit;

import ch.swisssmp.adventuredungeons.AdventureDungeons;

public class DungeonCountdownTask implements Runnable{

	private final DungeonInstance dungeonInstance;
	private final int countdown;
	
	public DungeonCountdownTask(DungeonInstance dungeonInstance, int countdown){
		this.dungeonInstance = dungeonInstance;
		this.countdown = countdown;
	}
	
	@Override
	public void run() {
		dungeonInstance.countdownTask = null;
		if(dungeonInstance.playersReady()){
			if(this.countdown>0){
				dungeonInstance.sendTitle(String.valueOf(this.countdown));
				Runnable countdownTask = new DungeonCountdownTask(this.dungeonInstance, this.countdown-1);
				dungeonInstance.countdownTask = Bukkit.getScheduler().runTaskLater(AdventureDungeons.plugin, countdownTask, 20);
			}
			else{
				dungeonInstance.start();
			}
		}
	}
}
