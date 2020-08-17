package ch.swisssmp.transformations;

import java.io.File;
import java.util.*;

import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.webcore.HTTPRequest;
import ch.swisssmp.world.WorldManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class TransformationContainer {

	private final World world;
	private final Set<AreaTransformation> transformations = new HashSet<>();
	
	private TransformationContainer(World world){
		this.world = world;
	}
	
	public World getWorld(){
		return this.world;
	}
	
	public Optional<AreaTransformation> getTransformation(UUID transformationUid){
		return transformations.stream().filter(t->t.getUniqueId().equals(transformationUid)).findAny();
	}
	
	protected Collection<AreaTransformation> getTransformations(){
		return transformations;
	}

	public void unload(){
		for(AreaTransformation transformation : transformations){
			transformation.unload();
		}
		transformations.clear();
	}
	
	public static TransformationContainer get(World world){
		return TransformationContainers.getContainer(world);
	}

	public static TransformationContainer load(World world){
		TransformationContainer result = new TransformationContainer(world);
		File file = getTransformationsFile(world);
		if(!file.exists()) return result;
		JsonObject json = JsonUtil.parse(file);
		if(json==null || !json.has("transformations")) return result;
		JsonArray transformationsArray = json.getAsJsonArray("transformations");
		for(JsonElement element : transformationsArray){
			if(!element.isJsonObject()) continue;
			JsonObject transformationSection = element.getAsJsonObject();
			AreaTransformation transformation = AreaTransformation.load(result, transformationSection).orElse(null);
			if(transformation==null) continue;
			result.transformations.add(transformation);
		}

		return result;
	}

	protected static File getTransformationsFile(World world){
		return WorldManager.getPluginDirectory(AreaTransformationsPlugin.getInstance(), world);
	}
}
