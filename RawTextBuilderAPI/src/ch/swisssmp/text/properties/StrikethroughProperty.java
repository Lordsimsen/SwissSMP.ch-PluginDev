package ch.swisssmp.text.properties;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * Boolean (true/false) - whether to render text with a strikethrough. Defaults to false.
 */
public class StrikethroughProperty extends AbstractBooleanProperty implements IOptionalProperty {

	private StrikethroughProperty(StrikethroughProperty template) {
		super(template);
	}
	
	public StrikethroughProperty() {
		super(true);
	}
	
	public StrikethroughProperty(boolean value) {
		super(value);
	}

	@Override
	public String getKey() {
		return "strikethrough";
	}

	@Override
	public IProperty duplicate() {
		return new StrikethroughProperty(this);
	}

	@Override
	public void applySpigotValues(BaseComponent component) {
		component.setStrikethrough(value);
	}
}
