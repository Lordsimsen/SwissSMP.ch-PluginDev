package ch.swisssmp.text.properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ch.swisssmp.text.RawTextObject;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

/**
 * Allows for a tooltip to be displayed when the player hovers their mouse over text.
 */
public class HoverEventProperty implements IOptionalProperty {

	private Action action;
	private String stringValue;
	private RawTextObject value;
	
	private HoverEventProperty(HoverEventProperty template) {
		this.action = template.action;
		this.stringValue = template.stringValue;
		this.value = template.value!=null ? (RawTextObject) template.value.duplicate() : null;
	}
	
	/**
	 * @param action: The type of tooltip to show. Valid values are "show_text" (shows raw JSON text), "show_item" (shows the tooltip of an item that can have NBT tags), and "show_entity" (shows an entity's name, possibly its type, and its UUID).
	 * @param value: The formatting of this tag varies depending on the action. Note that "show_text" is the only action to support an Object as the value; all other action values are Strings and should thus be wrapped in quotes.
	 */
	public HoverEventProperty(Action action, String value) {
		this.action = action;
		this.stringValue = value;
	}

	/**
	 * @param textContent: Shows a text object; automatically sets Action to "show_text".
	 */
	public HoverEventProperty(RawTextObject textContent) {
		this.action = Action.SHOW_TEXT;
		this.value = textContent;
	}
	
	@Override
	public String getKey() {
		return "hoverEvent";
	}

	@Override
	public JsonElement serialize() {
		JsonObject result = new JsonObject();
		result.addProperty("action", action.serializedValue);
		if(action==Action.SHOW_TEXT && value!=null) result.add("value", value.serialize());
		else result.addProperty("value", stringValue);
		return result;
	}
	
	public enum Action{
		/**
		 * Shows raw JSON text
		 */
		SHOW_TEXT("show_text"),
		/**
		 * Shows the tooltip of an item that can have NBT tags
		 */
		SHOW_ITEM("show_item"),
		/**
		 * Shows an entity's name, possibly its type, and its UUID
		 */
		SHOW_ENTITY("show_entity"),
		/**
		 * Shows an entity's name, possibly its type, and its UUID
		 */
		SHOW_ACHIEVEMENT("show_achievement");
		
		public final String serializedValue;
		
		private Action(String serializedValue) {
			this.serializedValue = serializedValue;
		}
		
		public HoverEvent.Action toSpigot(){
			switch(this) {
			case SHOW_TEXT: return HoverEvent.Action.SHOW_TEXT;
			case SHOW_ITEM: return HoverEvent.Action.SHOW_ITEM;
			case SHOW_ENTITY: return HoverEvent.Action.SHOW_ENTITY;
			case SHOW_ACHIEVEMENT: return HoverEvent.Action.SHOW_ACHIEVEMENT;
			default: return null;
			}
		}
	}

	@Override
	public IProperty duplicate() {
		return new HoverEventProperty(this);
	}

	@Override
	public void applySpigotValues(BaseComponent component) {
		HoverEvent hoverEvent;
		if(action==Action.SHOW_TEXT && value!=null) hoverEvent = new HoverEvent(action.toSpigot(), new BaseComponent[] {value.toSpigot()});
		else hoverEvent = new HoverEvent(action.toSpigot(), new ComponentBuilder(stringValue).create());
		component.setHoverEvent(hoverEvent);
	}
}
