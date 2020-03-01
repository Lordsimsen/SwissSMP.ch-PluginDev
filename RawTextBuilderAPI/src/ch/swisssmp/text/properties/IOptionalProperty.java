package ch.swisssmp.text.properties;

import net.md_5.bungee.api.chat.BaseComponent;

public interface IOptionalProperty extends IProperty {
	void applySpigotValues(BaseComponent component);
}
