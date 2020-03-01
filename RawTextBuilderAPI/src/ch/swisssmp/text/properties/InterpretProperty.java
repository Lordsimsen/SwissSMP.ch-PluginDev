package ch.swisssmp.text.properties;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * A boolean to indicate whether the game should interpret the SNBT value at the path indicated by  nbt as a raw JSON text (according to this raw JSON text structure). Useless otherwise.
 */
public class InterpretProperty extends AbstractBooleanProperty implements IOptionalProperty {

	private InterpretProperty(InterpretProperty template) {
		super(template);
	}
	
	public InterpretProperty(boolean value) {
		super(value);
	}

	@Override
	public String getKey() {
		return "interpret";
	}

	@Override
	public IProperty duplicate() {
		return new InterpretProperty(this);
	}

	@Override
	public void applySpigotValues(BaseComponent component) {
		System.out.println("InterpretProperty cannot be applied to chat components.");
	}

}
