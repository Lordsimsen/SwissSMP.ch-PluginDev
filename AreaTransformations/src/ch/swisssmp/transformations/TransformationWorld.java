package ch.swisssmp.transformations;

import java.util.HashMap;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class TransformationWorld{
	private static HashMap<World,TransformationWorld> worlds = new HashMap<World,TransformationWorld>();
	private HashMap<Integer,TransformationArea> transformations = new HashMap<Integer,TransformationArea>();
	
	private final World world;
	private String key;
	private boolean initialized = false;
	
	private TransformationWorld(World world){
		this.world = world;
		this.key = world.getName();
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
		this.loadTransformations(this.key);
	}
	
	public void loadTransformations(String key){
		//cleanup duty
		this.unloadTransformations();
		//initialize
		try{
			this.key = key;
			YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("transformations/get.php", new String[]{
					"world="+key
			});
			for(String IDstring : yamlConfiguration.getKeys(false)){
				ConfigurationSection dataSection = yamlConfiguration.getConfigurationSection(IDstring);
				transformations.put(dataSection.getInt("transformation_id"), new TransformationArea(this, dataSection));
			}
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
