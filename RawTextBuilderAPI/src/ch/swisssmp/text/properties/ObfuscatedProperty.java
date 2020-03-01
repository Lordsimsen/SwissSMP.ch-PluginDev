package ch.swisssmp.text.properties;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * Boolean (true/false) - whether to render text obfuscated. Defaults to false.
 */
public class ObfuscatedProperty extends AbstractBooleanProperty implements IOptionalProperty {

	private ObfuscatedProperty(ObfuscatedProperty template) {
		super(template);
	}
	
	public ObfuscatedProperty() {
		super(true);
	}
	
	public ObfuscatedProperty(boolean value) {
		super(value);
	}

	@Override
	public String getKey() {
		return "obfuscated";
	}

	@Override
	public IProperty duplicate() {
		return new ObfuscatedProperty(this);
	}

	@Override
	public void applySpigotValues(BaseComponent component) {
		component.setObfuscated(value);
	}

}
