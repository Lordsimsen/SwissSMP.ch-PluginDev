package ch.swisssmp.transformations;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import ch.swisssmp.transformations.logic.LogicSystem;
import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.world.WorldManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.World;

public class TransformationState {

	private final AreaTransformation transformation;
	private final UUID uid;
	private String name;
	private final LogicSystem logic = new LogicSystem();
	
	public TransformationState(AreaTransformation areaTransformation, UUID uid){
		this.transformation = areaTransformation;
		this.uid = uid;
	}

	public UUID getUniqueId(){
		return uid;
	}
	
	public File getSchematicFile(){
		File directory = WorldManager.getPluginDirectory(AreaTransformationsPlugin.getInstance(), transformation.getWorld());
		return new File(directory, uid+".schematic");
	}

	public World getWorld(){
		return transformation.getWorld();
	}
	
	public LogicSystem getLogicSystem(){
		return this.logic;
	}

	protected void load(JsonObject json){
		this.name = JsonUtil.getString("name", json);
		if(json.has("logic")) logic.load(json.getAsJsonObject("logic"));
	}

	protected JsonObject save(){
		JsonObject json = new JsonObject();
		JsonUtil.set("uid", uid, json);
		JsonUtil.set("name", name, json);
		json.add("logic", logic.save());
		return json;
	}
}
