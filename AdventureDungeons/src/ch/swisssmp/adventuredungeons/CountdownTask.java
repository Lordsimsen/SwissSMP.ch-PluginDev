package ch.swisssmp.adventuredungeons;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class CountdownTask implements Runnable{

	private final DungeonInstance dungeonInstance;
	private int countdown = 3;
	
	private BukkitTask task;
	
	private CountdownTask(DungeonInstance dungeonInstance){
		this.dungeonInstance = dungeonInstance;
	}
	
	@Override
	public void run() {
		if(dungeonInstance.getPlayerManager().arePlayersReady()){
			if(this.countdown>0){
				dungeonInstance.getPlayerManager().sendTitle(String.valueOf(this.countdown));
				this.countdown--;
			}
			else{
				task.cancel();
				dungeonInstance.start();
			}
		}
	}
	
	public void cancel(){
		this.task.cancel();
	}
	
	public static CountdownTask runCountdown(DungeonInstance dungeonInstance){
		CountdownTask countdownTask = new CountdownTask(dungeonInstance);
		countdownTask.task = Bukkit.getScheduler().runTaskTimer(AdventureDungeons.getInstance(), countdownTask, 0, 20);
		return countdownTask;
	}
}
