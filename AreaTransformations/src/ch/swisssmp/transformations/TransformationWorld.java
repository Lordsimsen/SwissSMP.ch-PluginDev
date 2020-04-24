package ch.swisssmp.transformations;

import java.util.HashMap;

import ch.swisssmp.webcore.HTTPRequest;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class TransformationWorld{
	private static HashMap<World,TransformationWorld> worlds = new HashMap<World,TransformationWorld>();
	private HashMap<Integer,TransformationArea> transformations = new HashMap<Integer,TransformationArea>();
	private HashMap<String,TransformationArea> transformationEnumMap = new HashMap<String,TransformationArea>();
	
	private final World world;
	private String world_name;
	private boolean initialized = false;
	
	private TransformationWorld(World world){
		this.world = world;
		this.world_name = world.getName();
		worlds.put(this.world, this);
	}
	
	public static TransformationWorld loadWorld(World world){
		if(!worlds.containsKey(world))
			return new TransformationWorld(world);
		return null;
	}
	
	public World getWorld(){
		return this.world;
	}
	
	public void loadTransformations(){
		this.loadTransformations(this.world_name);
	}
	
	public void loadTransformations(String world){
		//cleanup duty
		this.unloadTransformations();
		//initialize
		try{
			this.world_name = world;
			HTTPRequest request = DataSource.getResponse(AreaTransformations.getInstance(), "get.php", new String[]{
					"world="+world
			});
			request.onFinish(()->{
				YamlConfiguration yamlConfiguration = request.getYamlResponse();
				if(yamlConfiguration==null || !yamlConfiguration.contains("transformations")) return;
				ConfigurationSection transformationsSection = yamlConfiguration.getConfigurationSection("transformations");
				ConfigurationSection transformationSection;
				TransformationArea transformationArea;
				for(String key : transformationsSection.getKeys(false)){
					transformationSection = transformationsSection.getConfigurationSection(key);
					transformationArea = new TransformationArea(this, transformationSection);
					transformations.put(transformationArea.getTransformationId(), transformationArea);
					transformationEnumMap.put(transformationArea.getTransformationEnum(), transformationArea);
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void unloadTransformations(){
		for(TransformationArea oldArea : transformations.values()){
			for(AreaState oldState : oldArea.getSchematics()){
				for(TransformationLogic oldLogic : oldState.getLogicGates()){
					HandlerList.unregisterAll(oldLogic);
				}
			}
		}
		transformations.clear();
		transformationEnumMap.clear();
	}
	
	public boolean setTransformation(int transformation_id, int state, Player player){
		TransformationArea area = transformations.get(transformation_id);
		if(area==null) return false;
		return area.set(state, player);
	}
	
	public TransformationArea getTransformation(int transformation_id){
		if(!this.initialized){
			this.loadTransformations();
		}
		return transformations.get(transformation_id);
	}
	
	public TransformationArea getTransformation(String transformation_enum){
		if(!this.initialized){
			this.loadTransformations();
		}
		return transformationEnumMap.get(transformation_enum.toUpperCase());
	}
	
	protected TransformationArea[] getTransformations(){
		if(!this.initialized){
			this.loadTransformations();
		}
		return transformations.values().toArray(new TransformationArea[transformations.size()]);
	}
	
	public static void unloadWorld(World world){
		TransformationWorld transformationWorld = worlds.get(world);
		if(transformationWorld==null) return;
		worlds.remove(world);
		transformationWorld.unloadTransformations();
	}
	
	public static TransformationWorld get(World world){
		return worlds.get(world);
	}
	
	public static TransformationWorld[] getWorlds(){
		return worlds.values().toArray(new TransformationWorld[worlds.size()]);
	}
}
