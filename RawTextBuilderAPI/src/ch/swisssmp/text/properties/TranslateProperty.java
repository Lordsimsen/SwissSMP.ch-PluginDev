package ch.swisssmp.text.properties;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;

/**
 * The translation identifier of text to be displayed using the player's selected language. This identifier is the same as the identifiers found in lang files from assets or resource packs. Ignored when text exist in the root object.
 */
public class TranslateProperty extends AbstractStringProperty implements IMainProperty {

	private TranslateProperty(TranslateProperty template) {
		super(template);
	}
	
	public TranslateProperty(String value) {
		super(value);
	}

	@Override
	public String getKey() {
		return "translate";
	}

	@Override
	public IProperty duplicate() {
		return new TranslateProperty(this);
	}

	@Override
	public BaseComponent toSpigot() {
		return new TranslatableComponent(this.value);
	}

}
