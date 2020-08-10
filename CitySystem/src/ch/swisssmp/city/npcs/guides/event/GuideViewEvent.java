package ch.swisssmp.city.npcs.guides.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ch.swisssmp.city.npcs.guides.AddonGuideView;

public class GuideViewEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
	
    private final AddonGuideView view;
    
	public GuideViewEvent(AddonGuideView view) {
		this.view = view;
	}
	
	public AddonGuideView getView(){
		return view;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}
}
