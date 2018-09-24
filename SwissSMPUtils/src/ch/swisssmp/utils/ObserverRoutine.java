package ch.swisssmp.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class ObserverRoutine implements Runnable{

	private final ObservableRoutine routine;
	private final Player player;
	
	private BukkitTask task;
	
	private ObserverRoutine(ObservableRoutine routine, Player player){
		this.routine = routine;
		this.player = player;
	}

	@Override
	public void run() {
		SwissSMPler.get(player).sendActionBar(ChatColor.GREEN+""+Mathf.roundToInt(Mathf.clamp01(routine.getProgress())*100)+"% "+ChatColor.WHITE+routine.getProgressLabel());
		if(routine.getProgress()>=1){
			this.task.cancel();
		}
	}
	
	protected static ObserverRoutine run(ObservableRoutine routine, Player player){
		ObserverRoutine result = new ObserverRoutine(routine, player);
		result.task = Bukkit.getScheduler().runTaskTimer(SwissSMPUtils.plugin, result, 0, 1);
		return result;
	}
}
