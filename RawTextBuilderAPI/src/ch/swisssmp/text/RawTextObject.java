package ch.swisssmp.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import ch.swisssmp.text.properties.IProperty;
import ch.swisssmp.text.properties.IMainProperty;
import ch.swisssmp.text.properties.IOptionalProperty;
import ch.swisssmp.text.properties.TextProperty;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * The base chat component object.
 */
public class RawTextObject {
	private String text;
	private IMainProperty mainProperty;
	private final List<IOptionalProperty> properties;
	
	private RawTextObject(RawTextObject template) {
		this.text = template.text;
		this.mainProperty = template.mainProperty!=null ? (IMainProperty) template.mainProperty.duplicate() : null;
		this.properties = new ArrayList<IOptionalProperty>();
		for(IOptionalProperty p : template.properties) {
			this.properties.add((IOptionalProperty) p.duplicate());
		}
	}
	
	public RawTextObject() {
		this.properties = new ArrayList<IOptionalProperty>();
	}
	
	public RawTextObject(IMainProperty mainProperty) {
		this();
		this.mainProperty = mainProperty;
	}
	
	public RawTextObject(IMainProperty mainProperty, List<IOptionalProperty> properties) {
		this.mainProperty = mainProperty;
		this.properties = properties;
	}
	
	public RawTextObject(String text, IOptionalProperty... properties) {
		this.mainProperty = new TextProperty(text);
		this.properties = new ArrayList<IOptionalProperty>();
		for(IOptionalProperty p : properties) {
			this.properties.add(p);
		}
	}
	
	public RawTextObject(IMainProperty mainProperty, IOptionalProperty... properties) {
		this.mainProperty = mainProperty;
		this.properties = new ArrayList<IOptionalProperty>();
		for(IOptionalProperty p : properties) {
			this.properties.add(p);
		}
	}
	
	public RawTextObject(String text) {
		this();
		this.text = text;
	}
	
	public IMainProperty getMainProperty() {
		return mainProperty;
	}
	
	public Optional<IOptionalProperty> get(String key) {
		if(properties==null) return Optional.empty();
		return properties.stream().filter(p->p.getKey().equals(key)).findAny();
	}
	
	public void add(IOptionalProperty p) {
		if(text!=null && mainProperty==null) {
			// convert to complex object
			this.mainProperty = new TextProperty(text);
			text = null;
		}
		Optional<IOptionalProperty> existing = get(p.getKey());
		if(existing.isPresent()) remove(existing.get());
		properties.add(p);
	}
	
	public void remove(IOptionalProperty p) {
		properties.remove(p);
	}
	
	public JsonElement serialize() {
		if(properties.size()==0 && text!=null) {
			return new JsonPrimitive(text);
		}
		JsonObject result = new JsonObject();
		if(mainProperty!=null) result.add(mainProperty.getKey(), mainProperty.serialize());
		for(IProperty p : properties) {
			result.add(p.getKey(), p.serialize());
		}
		return result;
	}

	public RawTextObject duplicate() {
		return new RawTextObject(this);
	}
	
	@Override
	public String toString() {
		return serialize().toString();
	}
	
	public BaseComponent toSpigot() {
		if(text!=null && mainProperty==null) {
			return new TextComponent(text);
		}
		BaseComponent result = mainProperty.toSpigot();
		for(IOptionalProperty property : this.properties) {
			property.applySpigotValues(result);
		}
		return result;
	}
}
