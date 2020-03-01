package ch.swisssmp.text.properties;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * Boolean (true/false) - whether to render text underlined. Defaults to false.
 */
public class UnderlinedProperty extends AbstractBooleanProperty implements IOptionalProperty {

	private UnderlinedProperty(UnderlinedProperty template) {
		super(template);
	}
	
	public UnderlinedProperty() {
		super(true);
	}
	
	public UnderlinedProperty(boolean value) {
		super(value);
	}

	@Override
	public String getKey() {
		return "underlined";
	}

	@Override
	public IProperty duplicate() {
		return new UnderlinedProperty(this);
	}

	@Override
	public void applySpigotValues(BaseComponent component) {
		component.setUnderlined(value);
	}
}
