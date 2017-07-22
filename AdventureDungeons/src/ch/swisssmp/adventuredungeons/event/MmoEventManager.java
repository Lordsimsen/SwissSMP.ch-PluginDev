package ch.swisssmp.adventuredungeons.event;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

public class MmoEventManager{
	private final List<MmoEventListener> listeners = new ArrayList<MmoEventListener>();
	
	public MmoEventManager(JavaPlugin plugin){
		listeners.add(new MmoEventListenerBlock(plugin));
		listeners.add(new MmoEventListenerEntity(plugin));
		listeners.add(new MmoEventListenerInventory(plugin));
		listeners.add(new MmoEventListenerPlayer(plugin));
	}
	
	public static void callEvent(Event event){
		Bukkit.getServer().getPluginManager().callEvent(event);
	}
}
