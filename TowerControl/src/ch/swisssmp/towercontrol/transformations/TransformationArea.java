package ch.swisssmp.towercontrol.transformations;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class TransformationArea {
	private final World world;
	private final int transformation_id;
	private final String transformation_enum;
	private final String name;
	private String lastSchematic = "";
	private final HashMap<String, AreaState> schematics = new HashMap<String, AreaState>();
	
	public TransformationArea(World world, ConfigurationSection dataSection){
		this.world = world;
		this.transformation_id = dataSection.getInt("transformation_id");
		this.name = dataSection.getString("name");
		this.transformation_enum = dataSection.getString("enum");
		ConfigurationSection schematicsSection = dataSection.getConfigurationSection("schematics");
		if(schematicsSection!=null){
			for(String key : schematicsSection.getKeys(false)){
				ConfigurationSection schematicSection = schematicsSection.getConfigurationSection(key);
				AreaState areaState = new AreaState(this, schematicSection);
				String schematicName = areaState.getSchematicName();
				schematics.put(schematicName, areaState);
			}
		}
	}
	
	public boolean set(String state){
		AreaState areaState = schematics.get(state);
		if(areaState==null)
			return false;
		return areaState.trigger();
	}
	
	public void setLastSchematic(String lastSchematic){
		this.lastSchematic = lastSchematic;
	}
	
	public int getTransformationId(){
		return this.transformation_id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getTransformationEnum(){
		return this.transformation_enum;
	}
	
	public String getLastSchematic(){
		return this.lastSchematic;
	}
	
	public AreaState getSchematic(String schematicName){
		return this.schematics.get(schematicName);
	}
	
	public AreaState[] getSchematics(){
		return this.schematics.values().toArray(new AreaState[this.schematics.size()]);
	}
	
	public World getWorld(){
		return this.world;
	}
	
	public static TransformationArea load(World world, int transformation_area_id){
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("transformations/get.php", new String[]{
			"transformation="+transformation_area_id	
		});
		if(yamlConfiguration==null||!yamlConfiguration.contains("transformation")){
			Bukkit.getLogger().info("[TowerControl] Konnte Transformation mit der ID "+transformation_area_id+" nicht laden.");
			return null;
		}
		return new TransformationArea(world, yamlConfiguration.getConfigurationSection("transformation"));
	}
}
