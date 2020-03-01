package ch.swisssmp.text.properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.KeybindComponent;

/**
 * A string that can be used to display the key needed to preform a certain action. An example is key.inventory which always displays "E" unless the player has set a different key for opening their inventory.
 */
public class KeybindProperty implements IProperty, IMainProperty {

	private Key key;
	
	private KeybindProperty(KeybindProperty template) {
		this.key = template.key;
	}
	
	public KeybindProperty(Key key) {
		this.key = key;
	}
	
	@Override
	public String getKey() {
		return "keybind";
	}

	@Override
	public JsonElement serialize() {
		return new JsonPrimitive(key.serializedValue);
	}

	public enum Key{
		ATTACK("key.attack"),
		USE("key.use"),
		FORWARD("key.forward"),
		LEFT("key.left"),
		BACK("key.back"),
		RIGHT("key.right"),
		JUMP("key.jump"),
		SNEAK("key.sneak"),
		SPRINT("key.sprint"),
		DROP("key.drop"),
		INVENTORY("key.inventory"),
		CHAT("key.chat"),
		PLAYER_LIST("key.playerlist"),
		PICK_ITEM("key.pickItem"),
		COMMAND("key.command"),
		SCREENSHOT("key.screenshot"),
		TOGGLE_PERSPECTIVE("key.togglePerspective"),
		SMOOTH_CAMERA("key.smooth_camera"),
		FULLSCREEN("key.fullscreen"),
		SPECTATOR_OUTLINES("key.spectatorOutlines"),
		SWAP_HANDS("key.swapHands"),
		SAVE_TOOLBAR_ACTIVATOR("key.saveToolbarActivator"),
		LOAD_TOOLBAR_ACTIVATOR("key.loadToolbarActivator"),
		ADVANCEMENTS("key.advancements"),
		HOTBAR_1("key.hotbar.1"),
		HOTBAR_2("key.hotbar.2"),
		HOTBAR_3("key.hotbar.3"),
		HOTBAR_4("key.hotbar.4"),
		HOTBAR_5("key.hotbar.5"),
		HOTBAR_6("key.hotbar.6"),
		HOTBAR_7("key.hotbar.7"),
		HOTBAR_8("key.hotbar.8"),
		HOTBAR_9("key.hotbar.9"),
		;
		
		public final String serializedValue;
		
		private Key(String serializedValue) {
			this.serializedValue = serializedValue;
		}
	}

	@Override
	public IProperty duplicate() {
		return new KeybindProperty(this);
	}

	@Override
	public BaseComponent toSpigot() {
		return new KeybindComponent(this.key.serializedValue);
	}
}
