package ch.swisssmp.towercontrol.transformations;

import org.bukkit.Location;
import org.bukkit.World;

import ch.swisssmp.utils.ConfigurationSection;

public class AreaState {
	private final TransformationArea transformationArea;
	private final String schematicName;
	private final World world;
	private final int x;
	private final int y;
	private final int z;
	
	public AreaState(TransformationArea transformationArea, ConfigurationSection dataSection){
		this.transformationArea = transformationArea;
		this.schematicName = dataSection.getString("name");
		this.world = transformationArea.getWorld();
		this.x = dataSection.getInt("x");
		this.y = dataSection.getInt("y");
		this.z = dataSection.getInt("z");
	}
	
	public String getSchematicName(){
		return this.schematicName;
	}
	
	public Location getLocation(){
		return new Location(this.world, this.x, this.y, this.z);
	}
	
	public boolean trigger(){
		boolean success = SchematicUtil.paste(this.transformationArea.getTransformationEnum()+"/"+schematicName+".schematic", this.getLocation());
		if(success){
			if(this.transformationArea.getLastSchematic().equals(this.schematicName)) 
				return true;
			this.transformationArea.setLastSchematic(this.schematicName);
			return true;
		}
		else return false;
	}
}
