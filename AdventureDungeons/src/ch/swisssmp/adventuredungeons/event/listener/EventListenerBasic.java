package ch.swisssmp.adventuredungeons.event.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.world.DungeonInstance;

public abstract class EventListenerBasic implements Listener {

	private final EventListenerMaster master;
	public EventListenerBasic(EventListenerMaster master){
		this.master = master;
		Bukkit.getPluginManager().registerEvents(this, AdventureDungeons.getInstance());
	}
	protected DungeonInstance getInstance(){
		return this.master.getInstance();
	}
}
