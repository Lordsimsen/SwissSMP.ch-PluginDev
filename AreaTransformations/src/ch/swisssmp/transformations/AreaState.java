package ch.swisssmp.transformations;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.ConfigurationSection;

public class AreaState {
	private final TransformationArea transformationArea;
	private final String schematicName;
	private final World world;
	private final int x;
	private final int y;
	private final int z;
	private final ArrayList<TransformationLogic> logicGates = new ArrayList<TransformationLogic>();
	
	public AreaState(TransformationArea transformationArea, ConfigurationSection dataSection){
		this.transformationArea = transformationArea;
		this.schematicName = dataSection.getString("name");
		this.world = transformationArea.getTransformationWorld().getWorld();
		this.x = dataSection.getInt("x");
		this.y = dataSection.getInt("y");
		this.z = dataSection.getInt("z");

		ConfigurationSection logicsSection = dataSection.getConfigurationSection("sensors");
		if(logicsSection!=null){
			for(String key : logicsSection.getKeys(false)){
				ConfigurationSection logicSection = logicsSection.getConfigurationSection(key);
				logicGates.add(new TransformationLogic(this, logicSection, this.world));
			}
		}
	}
	
	public String getSchematicName(){
		return this.schematicName;
	}
	
	public Location getLocation(){
		return new Location(this.world, this.x, this.y, this.z);
	}
	
	public TransformationLogic[] getLogicGates(){
		return this.logicGates.toArray(new TransformationLogic[this.logicGates.size()]);
	}
	
	public boolean trigger(){
		return trigger(null);
	}
	
	public boolean trigger(Player player){
		if(!this.transformationArea.getLastSchematic().equals(this.schematicName)) {
			TransformationEvent event = new TransformationTriggerEvent(this.transformationArea, this, world, player);
			Bukkit.getPluginManager().callEvent(event);
			if(event.isCancelled()) return false;
		}
		boolean success = SchematicUtil.paste(this.transformationArea.getTransformationId()+"/"+schematicName+".schematic", this.getLocation());
		if(success){
			if(this.transformationArea.getLastSchematic().equals(this.schematicName)) 
				return true;
			this.transformationArea.setLastSchematic(this.schematicName);
			return true;
		}
		else return false;
	}
}
