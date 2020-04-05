package ch.swisssmp.ageofempires;

import java.util.Optional;

import ch.swisssmp.utils.ConfigurationSection;

public class TauntEntry {
	
	private final String key;
	private final String display;
	private final String audio;
	
	private TauntEntry(ConfigurationSection dataSection) {
		this.key = dataSection.getString("key");
		this.display = dataSection.getString("display");
		this.audio = dataSection.getString("audio");
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getDisplay() {
		return this.display;
	}
	
	public String getAudio() {
		return this.audio;
	}
	
	public static TauntEntry get(ConfigurationSection dataSection) {
		if(dataSection==null) return null;
		return new TauntEntry(dataSection);
	}
	
	public static Optional<TauntEntry> get(String key) {
		return TauntEntries.get(key);
	}
}
