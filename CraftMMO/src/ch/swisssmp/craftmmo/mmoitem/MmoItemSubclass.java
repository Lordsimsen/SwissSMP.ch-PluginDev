package ch.swisssmp.craftmmo.mmoitem;

import org.bukkit.configuration.ConfigurationSection;

public class MmoItemSubclass {
	public final String subclass_enum;
	public final String subclass_name;
	public final String subclass_tooltip;
	public final float subclass_speed;
	public final float subclass_strength;
	
	public MmoItemSubclass(ConfigurationSection dataSection){
		this.subclass_enum = dataSection.getString("enum");
		this.subclass_name = dataSection.getString("name");
		this.subclass_tooltip = dataSection.getString("tooltip");
		this.subclass_speed = (float)dataSection.getDouble("speed");
		this.subclass_strength = (float)dataSection.getDouble("strength");
	}
}
