package ch.swisssmp.text.properties;

import com.google.gson.JsonElement;

public interface IProperty {
	String getKey();
	JsonElement serialize();
	IProperty duplicate();
}
