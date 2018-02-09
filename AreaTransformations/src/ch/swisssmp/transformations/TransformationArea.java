package ch.swisssmp.transformations;

import java.util.HashMap;

import org.bukkit.World;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.ConfigurationSection;

public class TransformationArea {
	private final TransformationWorld transformationWorld;
	private final int transformation_id;
	private final String name;
	private String lastSchematic = "";
	private final HashMap<String, AreaState> schematics = new HashMap<String, AreaState>();
	
	public TransformationArea(TransformationWorld transformationWorld, ConfigurationSection dataSection){
		this.transformationWorld = transformationWorld;
		this.transformation_id = dataSection.getInt("transformation_id");
		this.name = dataSection.getString("name");
		ConfigurationSection schematicsSection = dataSection.getConfigurationSection("schematics");
		for(String key : schematicsSection.getKeys(false)){
			ConfigurationSection schematicSection = schematicsSection.getConfigurationSection(key);
			AreaState areaState = new AreaState(this, schematicSection);
			String schematicName = areaState.getSchematicName();
			schematics.put(schematicName, areaState);
		}
	}
	
	public boolean set(int state, Player player){
		AreaState areaState = schematics.get(state);
		if(areaState==null)
			return false;
		return areaState.trigger(player);
	}
	
	public void setLastSchematic(String lastSchematic){
		this.lastSchematic = lastSchematic;
	}
	
	public TransformationWorld getTransformationWorld(){
		return this.transformationWorld;
	}
	
	public int getTransformationId(){
		return this.transformation_id;
	}
	
	public String getName(){
		return this.name;
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
		return this.transformationWorld.getWorld();
	}
}
