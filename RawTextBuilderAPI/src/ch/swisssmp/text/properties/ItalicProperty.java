package ch.swisssmp.text.properties;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * Boolean (true/false) - whether to render text in italics. Defaults to false.
 */
public class ItalicProperty extends AbstractBooleanProperty implements IOptionalProperty {

	private ItalicProperty(ItalicProperty template) {
		super(template);
	}
	
	public ItalicProperty() {
		super(true);
	}
	
	public ItalicProperty(boolean value) {
		super(value);
	}

	@Override
	public String getKey() {
		return "italic";
	}

	@Override
	public IProperty duplicate() {
		return new ItalicProperty(this);
	}

	@Override
	public void applySpigotValues(BaseComponent component) {
		component.setItalic(value);
	}
}
