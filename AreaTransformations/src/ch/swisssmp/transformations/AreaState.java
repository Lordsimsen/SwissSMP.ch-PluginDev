package ch.swisssmp.transformations;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.ConfigurationSection;

public class AreaState {
	public final int transformation_id;
	public final String schematicName;
	private final String worldName;
	private final int x;
	private final int y;
	private final int z;
	public final ArrayList<TransformationLogic> logicGates = new ArrayList<TransformationLogic>();
	
	public AreaState(TransformationArea multiStateArea, ConfigurationSection dataSection){
		this.transformation_id = multiStateArea.transformation_id;
		this.schematicName = dataSection.getString("name");
		this.worldName = multiStateArea.worldName;
		this.x = dataSection.getInt("x");
		this.y = dataSection.getInt("y");
		this.z = dataSection.getInt("z");

		ConfigurationSection logicsSection = dataSection.getConfigurationSection("sensors");
		if(logicsSection!=null){
			for(String key : logicsSection.getKeys(false)){
				ConfigurationSection logicSection = logicsSection.getConfigurationSection(key);
				logicGates.add(new TransformationLogic(this, logicSection, this.worldName));
			}
		}
	}
	
	public Location getLocation(){
		return new Location(Bukkit.getWorld(this.worldName), this.x, this.y, this.z);
	}
	
	public boolean trigger(){
		return trigger(null);
	}
	
	public boolean trigger(Player player){
		TransformationEvent event = new TransformationTriggerEvent(TransformationArea.get(this.transformation_id), this, player);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) return false;
		boolean success = SchematicUtil.paste(transformation_id+"/"+schematicName+".schematic", this.getLocation());
		if(success){
			TransformationArea area = TransformationArea.get(this.transformation_id);
			if(area.lastSchematic.equals(this.schematicName)) 
				return true;
			area.lastSchematic = this.schematicName;
			return true;
		}
		else return false;
	}
}
