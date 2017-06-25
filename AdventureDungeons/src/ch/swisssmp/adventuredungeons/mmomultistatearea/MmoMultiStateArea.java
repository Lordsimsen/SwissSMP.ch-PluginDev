package ch.swisssmp.adventuredungeons.mmomultistatearea;

import java.util.HashMap;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import ch.swisssmp.adventuredungeons.mmoworld.MmoWorld;
import ch.swisssmp.adventuredungeons.mmoworld.MmoWorldInstance;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class MmoMultiStateArea {
	public final World world;
	public final int mmo_multistatearea_id;
	public final String name;
	protected String lastSchematic = "";
	public final HashMap<String, MmoAreaState> schematics = new HashMap<String, MmoAreaState>();
	
	public MmoMultiStateArea(MmoWorldInstance worldInstance, ConfigurationSection dataSection){
		this.world = worldInstance.world;
		this.mmo_multistatearea_id = dataSection.getInt("mmo_multistatearea_id");
		this.name = dataSection.getString("name");
		ConfigurationSection schematicsSection = dataSection.getConfigurationSection("schematics");
		for(String key : schematicsSection.getKeys(false)){
			ConfigurationSection schematicSection = schematicsSection.getConfigurationSection(key);
			MmoAreaState areaState = new MmoAreaState(this, schematicSection);
			String schematicName = areaState.schematicName;
			schematics.put(schematicName, areaState);
		}
		worldInstance.transformations.put(this.mmo_multistatearea_id, this);
	}
	
	public static boolean set(int mmo_multistatearea_id, int state, Player player){
		MmoWorldInstance worldInstance = MmoWorld.getInstance(player.getWorld());
		if(worldInstance==null) return false;
		MmoMultiStateArea area = worldInstance.transformations.get(mmo_multistatearea_id);
		if(area==null) return false;
		return area.set(state, player);
	}
	
	public boolean set(int state, Player player){
		MmoAreaState areaState = schematics.get(state);
		if(areaState==null)
			return false;
		return areaState.trigger(player);
	}
	
	public static void loadTransformations(MmoWorldInstance worldInstance, boolean initial) throws Exception{
		if(worldInstance==null) return;
		//cleanup duty
		for(MmoMultiStateArea oldArea : worldInstance.transformations.values()){
			for(MmoAreaState oldState : oldArea.schematics.values()){
				for(MmoMultiStateLogic oldLogic : oldState.logicGates){
					HandlerList.unregisterAll(oldLogic);
				}
			}
		}
		//initialize
		worldInstance.transformations = new HashMap<Integer, MmoMultiStateArea>();
		
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("multistateareas.php", new String[]{
				"world="+worldInstance.system_name
		});
		for(String IDstring : yamlConfiguration.getKeys(false)){
			ConfigurationSection dataSection = yamlConfiguration.getConfigurationSection(IDstring);
			new MmoMultiStateArea(worldInstance, dataSection);
		}
	}
}
