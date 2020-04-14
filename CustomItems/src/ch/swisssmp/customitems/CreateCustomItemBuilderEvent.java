package ch.swisssmp.customitems;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ch.swisssmp.utils.ConfigurationSection;

public class CreateCustomItemBuilderEvent extends Event {

	private final static HandlerList handlers = new HandlerList();
	
	private final CustomItemBuilder itemBuilder;
	private final ConfigurationSection dataSection;
	
	protected CreateCustomItemBuilderEvent(CustomItemBuilder itemBuilder, ConfigurationSection dataSection) {
		this.itemBuilder = itemBuilder;
		this.dataSection = dataSection;
	}
	
	public CustomItemBuilder getCustomItemBuilder() {
		return itemBuilder;
	}
	
	public ConfigurationSection getConfigurationSection() {
		return dataSection;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
