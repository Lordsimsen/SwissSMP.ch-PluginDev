package ch.swisssmp.adventuredungeons.event.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import ch.swisssmp.adventuredungeons.DungeonInstance;

public class EventListenerMaster{
	private final DungeonInstance dungeonInstance;
	private final List<EventListenerBasic> listeners = new ArrayList<EventListenerBasic>();
	
	public EventListenerMaster(DungeonInstance dungeonInstance){
		this.dungeonInstance = dungeonInstance;
		listeners.add(new EventListenerEntity(this));
		listeners.add(new EventListenerPlayer(this));
	}
	
	protected DungeonInstance getInstance(){
		return this.dungeonInstance;
	}
	
	public void unregister(){
		for(Listener listener : listeners){
			HandlerList.unregisterAll(listener);
		}
	}
}
