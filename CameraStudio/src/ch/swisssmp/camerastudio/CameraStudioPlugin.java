package ch.swisssmp.camerastudio;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class CameraStudioPlugin extends JavaPlugin {
	private static CameraStudioPlugin instance;

	public void onEnable() {

		instance = this;
		CameraStudio.init(this);

		getServer().getPluginManager().registerEvents(new EventListener(this), this);
		this.getCommand("cam").setExecutor(new CamCommand());
		getConfig().options().copyDefaults(true);
		saveConfig();

		CameraStudioWorlds.loadAll();
		for(Player player : Bukkit.getOnlinePlayers()){
			ViewerInfo info = ViewerInfo.load(player).orElse(null);
			if(info==null) continue;
			info.apply(player);
			info.delete();
		}

		Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
	}

	public void onDisable() {
		CameraStudio.inst().abortAll();
		CameraStudioWorlds.unloadAll();
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		Bukkit.getLogger().info(getDescription().getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
	}

	public static CameraStudioPlugin getInstance(){
		return instance;
	}
	public static String getPrefix(){
		return "["+ChatColor.YELLOW+ instance.getName()+ChatColor.RESET+"]";
	}
}