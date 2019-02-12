package ch.swisssmp.addonabnahme.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ch.swisssmp.addonabnahme.AddonInstanceGuideView;

public class GuideViewEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
	
    private final AddonInstanceGuideView view;
    
	public GuideViewEvent(AddonInstanceGuideView view) {
		this.view = view;
	}
	
	public AddonInstanceGuideView getView(){
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
