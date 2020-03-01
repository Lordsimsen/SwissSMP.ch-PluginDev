package ch.swisssmp.text.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import ch.swisssmp.text.selectors.ISelectorArgument;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.SelectorComponent;

/**
 * A string containing a selector (@p,@a,@r,@e or @s) and, optionally, selector arguments. Unlike text, the selector is translated into the correct player/entity names. 
 * If more than one player/entity is detected by the selector, it is displayed in a form such as 'Name1 and Name2' or 'Name1, Name2, Name3, and Name4'. 
 * Clicking a player's name inserted into a /tellraw command this way suggests a command to whisper to that player. 
 * Shift-clicking a player's name inserts that name into chat. Shift-clicking a non-player entity's name inserts its UUID into chat.
 */
public class SelectorProperty implements IProperty, IMainProperty {

	private Selector selector;
	List<ISelectorArgument> arguments;
	
	private SelectorProperty(SelectorProperty template) {
		this.selector = template.selector;
		this.arguments = new ArrayList<ISelectorArgument>();
		for(ISelectorArgument a : template.arguments) {
			this.arguments.add(a.duplicate());
		}
	}
	
	@Override
	public String getKey() {
		return "selector";
	}

	@Override
	public JsonElement serialize() {
		return new JsonPrimitive(this.getValue());
	}
	
	public String getValue() {
		return selector.serializedValue+"["+String.join(",", arguments.stream().map(a->a.getValue()).collect(Collectors.toList()))+"]";
	}

	public enum Selector{
		NEAREST_PLAYER("@p"),
		ALL_PLAYERS("@a"),
		RANDOM_PLAYER("@r"),
		ENTITIES("@e"),
		SELF("@s");
		
		public final String serializedValue;
		
		private Selector(String serializedValue) {
			this.serializedValue = serializedValue;
		}
		
		public static Selector parse(String s) {
			switch(s) {
			case "@p": return NEAREST_PLAYER;
			default: System.out.println("Unknown Selector "+s);
			return null;
			}
		}
	}

	@Override
	public IProperty duplicate() {
		return new SelectorProperty(this);
	}

	@Override
	public BaseComponent toSpigot() {
		return new SelectorComponent(this.getValue());
	}
}
