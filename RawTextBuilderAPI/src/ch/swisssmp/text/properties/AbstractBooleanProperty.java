package ch.swisssmp.text.properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public abstract class AbstractBooleanProperty implements IProperty {
	protected boolean value;
	
	protected AbstractBooleanProperty(AbstractBooleanProperty template) {
		this.value = template.value;
	}
	
	public AbstractBooleanProperty(boolean value) {
		this.value = value;
	}
	
	@Override
	public JsonElement serialize() {
		return new JsonPrimitive(this.value);
	}
}
