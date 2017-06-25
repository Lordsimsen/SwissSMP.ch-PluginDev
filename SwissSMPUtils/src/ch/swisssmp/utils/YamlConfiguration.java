package ch.swisssmp.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.bukkit.configuration.InvalidConfigurationException;

public class YamlConfiguration extends ConfigurationSection{
	public YamlConfiguration(String from){
		org.bukkit.configuration.file.YamlConfiguration yamlConfiguration = new org.bukkit.configuration.file.YamlConfiguration();
		try {
			yamlConfiguration.loadFromString(from);
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		this.configurationSection = yamlConfiguration;
	}
	public YamlConfiguration(){
		this.configurationSection = new org.bukkit.configuration.file.YamlConfiguration();
	}
	public boolean loadFromString(String from){
		try {
			((org.bukkit.configuration.file.YamlConfiguration)this.configurationSection).loadFromString(from);
			return true;
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			return false;
		}
	}
	public static YamlConfiguration loadConfiguration(File file) throws IOException{
		String configurationString = new String(Files.readAllBytes(file.toPath()));
		return (new YamlConfiguration(configurationString));
	}
	public void save(File file){
		try {
			((org.bukkit.configuration.file.YamlConfiguration)this.configurationSection).save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
