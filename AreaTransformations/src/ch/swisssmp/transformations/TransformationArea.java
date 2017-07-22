package ch.swisssmp.transformations;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class TransformationArea {
	private static HashMap<Integer,TransformationArea> transformations = new HashMap<Integer,TransformationArea>();
	public final String worldName;
	public final int transformation_id;
	public final String name;
	protected String lastSchematic = "";
	public final HashMap<String, AreaState> schematics = new HashMap<String, AreaState>();
	
	public TransformationArea(ConfigurationSection dataSection){
		this.worldName = dataSection.getString("world");
		this.transformation_id = dataSection.getInt("transformation_id");
		this.name = dataSection.getString("name");
		ConfigurationSection schematicsSection = dataSection.getConfigurationSection("schematics");
		for(String key : schematicsSection.getKeys(false)){
			ConfigurationSection schematicSection = schematicsSection.getConfigurationSection(key);
			AreaState areaState = new AreaState(this, schematicSection);
			String schematicName = areaState.schematicName;
			schematics.put(schematicName, areaState);
		}
	}
	
	public static boolean set(int transformation_id, int state, Player player){
		TransformationArea area = transformations.get(transformation_id);
		if(area==null) return false;
		return area.set(state, player);
	}
	
	public boolean set(int state, Player player){
		AreaState areaState = schematics.get(state);
		if(areaState==null)
			return false;
		return areaState.trigger(player);
	}
	
	public World getWorld(){
		return Bukkit.getWorld(this.worldName);
	}
	
	public static void loadTransformations(){
		//cleanup duty
		for(TransformationArea oldArea : transformations.values()){
			for(AreaState oldState : oldArea.schematics.values()){
				for(TransformationLogic oldLogic : oldState.logicGates){
					HandlerList.unregisterAll(oldLogic);
				}
			}
		}
		transformations.clear();
		//initialize
		
		try{
			YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("transformations/get.php");
			for(String IDstring : yamlConfiguration.getKeys(false)){
				ConfigurationSection dataSection = yamlConfiguration.getConfigurationSection(IDstring);
				transformations.put(dataSection.getInt("transformation_id"), new TransformationArea(dataSection));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected static TransformationArea[] getAll(){
		return transformations.values().toArray(new TransformationArea[transformations.size()]);
	}
	
	public static TransformationArea get(int area_id){
		return transformations.get(area_id);
	}
}
