package ch.swisssmp.adventuredungeons.mmoworld;

import org.bukkit.Bukkit;

import ch.swisssmp.adventuredungeons.Main;

public class MmoDungeonCountdownTask implements Runnable{

	private final MmoDungeonInstance dungeonInstance;
	private final int countdown;
	
	public MmoDungeonCountdownTask(MmoDungeonInstance dungeonInstance, int countdown){
		this.dungeonInstance = dungeonInstance;
		this.countdown = countdown;
	}
	
	@Override
	public void run() {
		dungeonInstance.countdownTask = null;
		if(dungeonInstance.playersReady()){
			if(this.countdown>0){
				dungeonInstance.sendTitle(String.valueOf(this.countdown));
				Runnable countdownTask = new MmoDungeonCountdownTask(this.dungeonInstance, this.countdown-1);
				dungeonInstance.countdownTask = Bukkit.getScheduler().runTaskLater(Main.plugin, countdownTask, 20);
			}
			else{
				dungeonInstance.start();
			}
		}
	}
}
