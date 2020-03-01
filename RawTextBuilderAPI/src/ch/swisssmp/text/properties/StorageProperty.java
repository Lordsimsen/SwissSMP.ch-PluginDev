package ch.swisssmp.text.properties;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * A string specifying the namespaced ID of the command storage from which the NBT value is obtained. Useless if nbt is absent.
 */
public class StorageProperty extends AbstractStringProperty implements IOptionalProperty {

	private StorageProperty(StorageProperty template) {
		super(template);
	}
	
	public StorageProperty(String value) {
		super(value);
	}

	@Override
	public String getKey() {
		return "storage";
	}

	@Override
	public IProperty duplicate() {
		return new StorageProperty(this);
	}

	@Override
	public void applySpigotValues(BaseComponent component) {
		System.out.println("StorageProperty cannot be applied to chat components.");
	}
}
