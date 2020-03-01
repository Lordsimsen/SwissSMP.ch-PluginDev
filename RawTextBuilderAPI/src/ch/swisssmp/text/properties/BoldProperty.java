package ch.swisssmp.text.properties;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * Boolean (true/false) - whether to render text in bold. Defaults to false.
 */
public class BoldProperty extends AbstractBooleanProperty implements IOptionalProperty{

	private BoldProperty(BoldProperty template) {
		super(template);
	}
	
	public BoldProperty() {
		super(true);
	}
	
	public BoldProperty(boolean value) {
		super(value);
	}

	@Override
	public String getKey() {
		return "bold";
	}

	@Override
	public IProperty duplicate() {
		return new BoldProperty(this);
	}

	@Override
	public void applySpigotValues(BaseComponent component) {
		component.setBold(value);
	}
}
