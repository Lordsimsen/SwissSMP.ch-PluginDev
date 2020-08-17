package ch.swisssmp.customitems;

import com.google.gson.JsonObject;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ch.swisssmp.utils.ConfigurationSection;

public class CreateCustomItemBuilderEvent extends Event {

	private final static HandlerList handlers = new HandlerList();
	
	private final CustomItemBuilder itemBuilder;
	private final JsonObject json;
	
	protected CreateCustomItemBuilderEvent(CustomItemBuilder itemBuilder, JsonObject json) {
		this.itemBuilder = itemBuilder;
		this.json = json;
	}
	
	public CustomItemBuilder getCustomItemBuilder() {
		return itemBuilder;
	}
	
	public JsonObject getJson() {
		return json;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
