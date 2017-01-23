package ch.swisssmp.adventuredungeons.mmoevent;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class MmoEventListener implements Listener{
	protected final JavaPlugin plugin;
	
	public MmoEventListener(JavaPlugin plugin){
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
}
