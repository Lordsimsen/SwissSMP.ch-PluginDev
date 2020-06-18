package ch.swisssmp.citymapdisplays;

import java.io.File;
import java.util.*;

import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;

public class CityMapDisplays {
	private static final List<CityMapDisplay> displays = new ArrayList<CityMapDisplay>();

	protected static Collection<CityMapDisplay> getAll(){
		return displays;
	}
	
	protected static void add(CityMapDisplay display) {
		displays.add(display);
	}
	
	protected static void remove(CityMapDisplay display) {
		displays.remove(display);
	}
	
	protected static Optional<CityMapDisplay> get(UUID displayUid){
		return displays.stream().filter(d->d.getUid().equals(displayUid)).findAny();
	}
	
	protected static void save() {
		JsonObject json = new JsonObject();
		JsonArray displaysArray = new JsonArray();
		for(CityMapDisplay display : displays) {
			JsonObject displayData = display.save();
			displaysArray.add(displayData);
		}
		json.add("displays", displaysArray);
		File file = getFile();
		JsonUtil.save(file, json);
	}
	
	protected static void load() {
		displays.clear();
		File file = getFile();
		if(!file.exists()) {
			Bukkit.getLogger().info(CityMapDisplaysPlugin.getPrefix()+" Cannot load existing CityMapDisplays because there is no save file.");
			return;
		}
		JsonObject json = JsonUtil.parse(file);
		if(json==null) {
			Bukkit.getLogger().info(CityMapDisplaysPlugin.getPrefix()+" Cannot load existing CityMapDisplays because the save file is invalid.");
			return;
		}
		JsonArray displaysArray = json.has("displays") ? json.getAsJsonArray("displays") : null;
		if(displaysArray!=null) {
			for(JsonElement element : displaysArray) {
				if(!element.isJsonObject()) continue;
				JsonObject displayData = element.getAsJsonObject();
				CityMapDisplay display = CityMapDisplay.load(displayData);
				displays.add(display);
			}
		}
	}
	
	protected static void unload() {
		for(CityMapDisplay display : displays) {
			display.unload();
		}
		displays.clear();
	}
	
	protected static File getFile() {
		return new File(CityMapDisplaysPlugin.getInstance().getDataFolder(), "displays.yml");
	}
}
