package ch.swisssmp.text.properties;

import com.google.gson.JsonElement;

public abstract class AbstractStringProperty implements IProperty {

	protected String value;
	
	public AbstractStringProperty(String value) {
		this.value = value;
	}
	
	protected AbstractStringProperty(AbstractStringProperty template) {
		this.value = template.value;
	}
	
	@Override
	public JsonElement serialize() {
		return new com.google.gson.JsonPrimitive(this.value);
	}
}
