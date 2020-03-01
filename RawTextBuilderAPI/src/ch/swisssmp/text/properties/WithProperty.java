package ch.swisssmp.text.properties;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import ch.swisssmp.text.RawTextObject;

/**
 * A list of chat component arguments and/or string arguments to be used by  translate. Useless otherwise.
 */
public class WithProperty implements IProperty {

	private List<RawTextObject> parts;
	
	private WithProperty(WithProperty template) {
		this.parts = new ArrayList<RawTextObject>();
		for(RawTextObject o : template.parts) {
			this.parts.add((RawTextObject) o.duplicate());
		}
	}
	
	public WithProperty(List<RawTextObject> parts) {
		this.parts = parts;
	}
	
	public List<RawTextObject> getParts(){
		return this.parts;
	}
	
	@Override
	public String getKey() {
		return "with";
	}

	@Override
	public JsonElement serialize() {
		JsonArray result = new JsonArray();
		for(RawTextObject part : parts) {
			result.add(part.serialize());
		}
		return result;
	}

	@Override
	public IProperty duplicate() {
		return new WithProperty(this);
	}
}
