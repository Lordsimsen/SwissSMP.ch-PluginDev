package ch.swisssmp.adventuredungeons.world;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.webcore.FTPStatus;

public class WorldTransferTask implements Runnable {
	
	private final CommandSender sender;
	private final String worldName;
	private final FTPStatus statusObserver;
	
	private BukkitTask task;
	
	private WorldTransferTask(CommandSender sender, String worldName, FTPStatus statusObserver){
		this.sender = sender;
		this.worldName = worldName;
		this.statusObserver = statusObserver;
	}
	
	@Override
	public void run() {
		if(!statusObserver.isDone()){
			if(sender instanceof Player) SwissSMPler.get((Player)sender).sendActionBar("Fortschritt: "+statusObserver.getProgress()+"%");
			return;
		}
		if(statusObserver.isError()) sender.sendMessage("[WorldManager] "+statusObserver.getError());
		else sender.sendMessage("[AdventureDungeons] "+ChatColor.GREEN+"Welt '"+worldName+"' erfolgreich transferiert.");
		task.cancel();
	}
	
	protected static WorldTransferTask run(CommandSender sender, String worldName, FTPStatus statusObserver){
		WorldTransferTask result = new WorldTransferTask(sender, worldName, statusObserver);
		BukkitTask task = Bukkit.getScheduler().runTaskTimer(AdventureDungeons.getInstance(), result, 1, 1);
		result.task = task;
		return result;
	}
}
