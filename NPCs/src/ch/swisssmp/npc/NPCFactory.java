package ch.swisssmp.npc;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import com.google.gson.JsonObject;

import ch.swisssmp.npc.modules.FactoryModule;

public class NPCFactory {
	private String identifier;
	private String displayName;
	private boolean displayNameVisible;
	
	private EntityType entityType;
	private JsonObject data;
	
	private List<FactoryModule> modules = new ArrayList<FactoryModule>();
	
	protected NPCFactory(EntityType entityType){
		this.entityType = entityType;
	}
	
	public void setIdentifier(String identifier){
		this.identifier = identifier;
	}
	
	public void setDisplayName(String displayName){
		this.displayName = displayName;
	}
	
	public void setDisplayNameVisible(boolean alwaysVisible){
		displayNameVisible = alwaysVisible;
	}
	
	public void setYamlConfiguration(JsonObject json){
		this.data = json;
	}
	
	public void addModule(FactoryModule module){
		modules.add(module);
	}
	
	public NPCInstance spawn(Location location){
		NPCInstance result = NPCInstance.create(entityType, location);
		if(identifier!=null) result.setIdentifier(identifier);
		if(displayName!=null && !displayName.isEmpty()){
			result.setName(displayName);
			result.setNameVisible(displayNameVisible);
		}
		if(data!=null){
			result.setJsonData(data);
		}
		for(FactoryModule module : modules){
			module.applyData(result);
		}
		return result;
	}
	
	public static NPCFactory get(EntityType entityType){
		switch(entityType){
		default: return new NPCFactory(entityType);
		}
	}
}
