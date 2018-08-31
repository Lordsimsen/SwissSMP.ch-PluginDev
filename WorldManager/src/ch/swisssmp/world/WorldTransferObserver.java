package ch.swisssmp.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.utils.SwissSMPler;

public class WorldTransferObserver implements Runnable {
	
	private final CommandSender sender;
	private final String worldName;
	private final WorldTransfer transfer;
	
	private BukkitTask task;

	private List<Runnable> onImmediateFinish = new ArrayList<Runnable>();
	private List<Runnable> onFinish = new ArrayList<Runnable>();
	
	private WorldTransferObserver(CommandSender sender, String worldName, WorldTransfer transfer){
		this.sender = sender;
		this.worldName = worldName;
		this.transfer = transfer;
	}
	
	@Override
	public void run() {
		this.showProgress(this.transfer.getStatusObserver()!=null ? this.transfer.getStatusObserver().getProgress() : 0);
		if(this.transfer.getStatusObserver()==null || !this.transfer.getStatusObserver().isDone()){
			return;
		}
		if(this.transfer.getStatusObserver().isError()) sender.sendMessage("[WorldManager] "+this.transfer.getStatusObserver().getError());
		else sender.sendMessage("[WorldManager] "+ChatColor.GREEN+"Welt '"+worldName+"' erfolgreich transferiert.");
		for(Runnable runnable : this.onImmediateFinish){
			Bukkit.getScheduler().runTaskLater(WorldManager.plugin, runnable, 1L);
		}
		for(Runnable runnable : this.onFinish){
			Bukkit.getScheduler().runTaskLater(WorldManager.plugin, runnable, 5L);
		}
		task.cancel();
	}
	
	private void showProgress(int progress){
		if(sender instanceof Player) SwissSMPler.get((Player)sender).sendActionBar("Fortschritt: "+progress+"%");
	}
	
	/**
	 * Used to execute and completely finish internal WorldManager code before everything else
	 * @param runnable - Runnable to run directly after the download finishes
	 */
	protected void addImmediateOnFinishListener(Runnable runnable){
		this.onImmediateFinish.add(runnable);
	}
	
	public void addOnFinishListener(Runnable runnable){
		this.onFinish.add(runnable);
	}
	
	protected static WorldTransferObserver run(CommandSender sender, String worldName, WorldTransfer transfer){
		WorldTransferObserver result = new WorldTransferObserver(sender, worldName, transfer);
		BukkitTask task = Bukkit.getScheduler().runTaskTimer(WorldManager.plugin, result, 1, 1);
		result.task = task;
		return result;
	}
}
