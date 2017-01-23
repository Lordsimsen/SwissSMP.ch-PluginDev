package ch.swisssmp.craftmmo.mmoitem;

import org.bukkit.configuration.ConfigurationSection;

public class MmoItemThrowable extends MmoItem{

	public final boolean bounce;
	public final int damage;
	
	public MmoItemThrowable(ConfigurationSection dataSection) throws Exception {
		super(dataSection);
		//configurationSection cannot be null because this class is only used when there is a classSection in it
		ConfigurationSection configurationSection = dataSection.getConfigurationSection("configuration");
		ConfigurationSection classSection = configurationSection.getConfigurationSection("class");
		this.bounce = (classSection.getInt("bounce")==1);
		this.damage = classSection.getInt("strength");
	}
}
