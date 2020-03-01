package ch.swisssmp.text.properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;

/**
 * Allows for events to occur when the player clicks on text.
 */
public class ClickEventProperty implements IOptionalProperty {

	private Action action;
	private String value;
	
	private ClickEventProperty(ClickEventProperty template) {
		this.action = template.action;
		this.value = template.value;
	}
	
	/**
	 * @param action: The action to perform when clicked.
	 * @param value: The URL, file, chat, command or book page used by the specified action. Note that commands must be prefixed with the usual "/" slash.
	 */
	public ClickEventProperty(Action action, String value) {
		this.action = action;
		this.value = value;
	}
	
	@Override
	public String getKey() {
		return "clickEvent";
	}

	@Override
	public JsonElement serialize() {
		JsonObject result = new JsonObject();
		result.addProperty("action", action.serializedValue);
		result.addProperty("value", value);
		return result;
	}

	public enum Action{
		/**
		 * Opens value as a URL in the player's default web browser.
		 */
		OPEN_URL("open_url"),
		/**
		 * Has value entered in chat as though the player typed it themselves. This can be used to run commands, provided the player has the required permissions.
		 */
		RUN_COMMAND("run_command"),
		/**
		 * Can be used only in written books, changes to page value if that page exists.
		 */
		CHANGE_PAGE("change_page"),
		/**
		 * Similar to "run_command" but it cannot be used in a written book, the text appears only in the player's chat input and it is not automatically entered. 
		 * Unlike insertion, this replaces the existing contents of the chat input.
		 */
		SUGGEST_COMMAND("suggest_command"),
		/**
		 * Copy the value to the clipboard.
		 */
		COPY_TO_CLIPBOARD("copy_to_clipboard");
		
		public final String serializedValue;
		
		private Action(String serializedValue) {
			this.serializedValue = serializedValue;
		}
		
		public ClickEvent.Action toSpigot(){
			switch(this) {
			case OPEN_URL: return ClickEvent.Action.OPEN_URL;
			case RUN_COMMAND: return ClickEvent.Action.RUN_COMMAND;
			case CHANGE_PAGE: return ClickEvent.Action.CHANGE_PAGE;
			case SUGGEST_COMMAND: return ClickEvent.Action.SUGGEST_COMMAND;
			case COPY_TO_CLIPBOARD: return ClickEvent.Action.COPY_TO_CLIPBOARD;
			default: return null;
			}
		}
	}

	@Override
	public IProperty duplicate() {
		return new ClickEventProperty(this);
	}

	@Override
	public void applySpigotValues(BaseComponent component) {
		component.setClickEvent(new ClickEvent(this.action.toSpigot(),this.value));
	}
}
