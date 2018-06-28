package ch.swisssmp.streets;

import java.util.HashMap;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class Street {
	private static HashMap<String,Street> streetsMap = new HashMap<String,Street>();
	private final int speed;
	private final String region_id;
	private final String street_label;
	
	private Street(ConfigurationSection dataSection){
		this.speed = dataSection.getInt("speed");
		this.region_id = dataSection.getString("region_id");
		this.street_label = dataSection.getString("street_label");
	}
	
	public String getRegionId(){
		return this.region_id;
	}
	
	public String getStreetLabel(){
		return this.street_label;
	}
	
	public int getSpeed(){
		return this.speed;
	}
	
	public static Street get(String region_id){
		if(region_id==null) return null;
		return streetsMap.get(region_id);
	}
	
	protected static void loadStreets(){
		streetsMap.clear();
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("streets/get.php");
		if(yamlConfiguration==null || !yamlConfiguration.contains("streets")) return;
		ConfigurationSection streetsSection = yamlConfiguration.getConfigurationSection("streets");
		ConfigurationSection streetSection;
		for(String key : streetsSection.getKeys(false)){
			streetSection = streetsSection.getConfigurationSection(key);
			streetsMap.put(streetSection.getString("region_id"), new Street(streetSection));
		}
	}
}
