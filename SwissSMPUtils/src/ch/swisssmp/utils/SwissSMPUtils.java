package ch.swisssmp.utils;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class SwissSMPUtils extends JavaPlugin{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static SwissSMPUtils plugin;
	protected static EventListener listener;
	protected static boolean debug;
	
	protected static BukkitTask afkRoutine;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		
		listener = new EventListener();
		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("balance").setExecutor(playerCommand);
		this.getCommand("seen").setExecutor(playerCommand);
		this.getCommand("afk").setExecutor(playerCommand);
		this.getCommand("list").setExecutor(playerCommand);
		this.getCommand("tell").setExecutor(playerCommand);
		this.getCommand("worlds").setExecutor(playerCommand);
		
		checkAfk();
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		afkRoutine.cancel();
		for(BukkitTask task : SwissSMPler.afk_tasks.values()){
			task.cancel();
		}
		listener.unregister();
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static void broadcastMessage(String message){
		for(Player player : Bukkit.getOnlinePlayers()){
			player.sendMessage(message);
		}
	}
	
	private void checkAfk(){
		SwissSMPler.checkAllAfk(false);
		afkRoutine = Bukkit.getScheduler().runTaskLater(this, new Runnable(){
			@Override
			public void run() {
				checkAfk();
			}
			
		}, 100L);
	}
}
