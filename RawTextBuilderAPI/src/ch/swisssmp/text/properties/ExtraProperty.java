package ch.swisssmp.text.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import ch.swisssmp.text.RawTextObject;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 * A list of additional objects, sharing the same format as the base object.
 */
public class ExtraProperty implements IOptionalProperty {
	private List<RawTextObject> parts;
	
	private ExtraProperty(ExtraProperty template) {
		this.parts = new ArrayList<RawTextObject>();
		for(RawTextObject o : template.parts) {
			this.parts.add((RawTextObject) o.duplicate());
		}
	}
	
	/**
	 * @param parts: A list element whose structure repeats this raw JSON text structure. Note that all properties of this object are inherited by children except for text, extra, translate, with, and score. This means that children retain the same formatting and events as this object unless they explicitly override them.
	 */
	public ExtraProperty(List<RawTextObject> parts) {
		this.parts = parts;
	}
	
	public ExtraProperty(RawTextObject... parts) {
		this.parts = new ArrayList<RawTextObject>();
		for(RawTextObject p : parts) {
			this.parts.add(p);
		}
	}
	
	public ExtraProperty() {
		this.parts = new ArrayList<RawTextObject>();
	}
	
	public List<RawTextObject> getParts(){
		return this.parts;
	}
	
	public void add(RawTextObject o) {
		parts.add(o);
	}
	
	public void remove(RawTextObject o) {
		parts.remove(o);
	}
	
	@Override
	public String getKey() {
		return "extra";
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
		return new ExtraProperty(this);
	}

	@Override
	public void applySpigotValues(BaseComponent component) {
		component.setExtra(this.parts.stream().map(p->p.toSpigot()).collect(Collectors.toList()));
	}
}
