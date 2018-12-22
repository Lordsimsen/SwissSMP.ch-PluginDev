package ch.swisssmp.resourcepack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerResourcePackUpdateEvent extends PlayerEvent{
    private static final HandlerList handlers = new HandlerList();
    
    private List<String> components = new ArrayList<String>();
    
	public PlayerResourcePackUpdateEvent(Player player) {
		super(player);
	}
	
	public void addComponent(String component){
		this.components.add(component);
	}
	
	public Collection<String> getComponents(){
		return Collections.unmodifiableCollection(this.components);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}
}
