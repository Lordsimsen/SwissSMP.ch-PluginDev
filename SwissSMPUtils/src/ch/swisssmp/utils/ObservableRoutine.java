package ch.swisssmp.utils;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public abstract class ObservableRoutine implements Runnable {

	private Collection<Runnable> onFinish = new ArrayList<Runnable>();
	private BukkitTask task;
	
	/**
	 * Returns the current progress
	 * @return A number representing the current progress between 0 and 1
	 */
	public abstract float getProgress();
	
	/**
	 * Returns a label describing the runnable
	 */
	public abstract String getProgressLabel();
	
	/**
	 * The Runnable is executed when the Runnable finishes
	 */
	public void addOnFinishListener(Runnable runnable) {
		this.onFinish.add(runnable);
	}
	
	public void addObserver(Player player){
		ObserverRoutine.run(this, player);
	}

	/**
	 * Run this when Runnable finishes
	 */
	protected void finish(){
		this.runOnFinishListeners();
		if(this.task!=null) this.task.cancel();
	}
	
	private void runOnFinishListeners(){
		for(Runnable runnable : this.onFinish){
			try{
				runnable.run();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param plugin
	 * @param startDelay
	 * @param runTimer
	 */
	public void start(Plugin plugin, long startDelay, long period){
		this.task = Bukkit.getScheduler().runTaskTimer(plugin, this, startDelay, period);
	}
}
