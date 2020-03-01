package ch.swisssmp.text.properties;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * A string specifying the target selector for the entity from which the NBT value is obtained. Useless if  nbt is absent.
 */
public class EntityProperty extends AbstractStringProperty implements IOptionalProperty{

	private EntityProperty(EntityProperty template) {
		super(template);
	}
	
	public EntityProperty(String value) {
		super(value);
	}

	@Override
	public String getKey() {
		return "entity";
	}

	@Override
	public IProperty duplicate() {
		return new EntityProperty(this);
	}

	@Override
	public void applySpigotValues(BaseComponent component) {
		System.out.println("EntityProperty cannot be applied to chat components.");
	}
}
