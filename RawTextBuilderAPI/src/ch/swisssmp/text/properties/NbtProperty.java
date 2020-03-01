package ch.swisssmp.text.properties;

import com.google.gson.JsonElement;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * A string indicating the NBT path used for looking up NBT values from an entity, a block entity or a command storage. Ignored when any of the previous fields exist in the root object.
 */
public class NbtProperty implements IMainProperty {

	private NbtProperty(NbtProperty template) {
		
	}
	
	@Override
	public String getKey() {
		return "nbt";
	}

	@Override
	public JsonElement serialize() {
		//TODO add code
		System.out.println("EntityProperty is not implemented yet.");
		return null;
	}

	@Override
	public IProperty duplicate() {
		return new NbtProperty(this);
	}

	@Override
	public BaseComponent toSpigot() {
		System.out.println("NbtProperty cannot be used as a chat component.");
		return null;
	}

}
