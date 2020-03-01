package ch.swisssmp.text.properties;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * A string representing raw text to display directly in chat. Can use escape characters, such as \n for newline (enter), \t for tab, etc.
 */
public class TextProperty extends AbstractStringProperty implements IMainProperty {

	private TextProperty(TextProperty template) {
		super(template);
	}
	
	public TextProperty(String value) {
		super(value);
		
	}

	@Override
	public String getKey() {
		return "text";
	}

	@Override
	public IProperty duplicate() {
		return new TextProperty(this);
	}

	@Override
	public BaseComponent toSpigot() {
		return new TextComponent(this.value);
	}

}
