package ch.swisssmp.text.properties;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * When the text is shift-clicked by a player, this string is inserted in their chat input. It does not overwrite any existing text the player was writing.
 *
 */
public class InsertionProperty extends AbstractStringProperty implements IOptionalProperty {

	private InsertionProperty(InsertionProperty template) {
		super(template);
	}
	
	public InsertionProperty(String value) {
		super(value);
	}

	@Override
	public String getKey() {
		return "insertion";
	}

	@Override
	public IProperty duplicate() {
		return new InsertionProperty(this);
	}

	@Override
	public void applySpigotValues(BaseComponent component) {
		component.setInsertion(value);
	}
}
