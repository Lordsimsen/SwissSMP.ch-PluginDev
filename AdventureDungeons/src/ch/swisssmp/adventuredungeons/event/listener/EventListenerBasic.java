package ch.swisssmp.adventuredungeons.event.listener;

import ch.swisssmp.adventuredungeons.AdventureDungeonsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.DungeonInstance;

public abstract class EventListenerBasic implements Listener {

	private final EventListenerMaster master;
	public EventListenerBasic(EventListenerMaster master){
		this.master = master;
		Bukkit.getPluginManager().registerEvents(this, AdventureDungeonsPlugin.getInstance());
	}
	protected DungeonInstance getInstance(){
		return this.master.getInstance();
	}
}
