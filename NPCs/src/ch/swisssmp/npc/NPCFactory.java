package ch.swisssmp.npc;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import ch.swisssmp.npc.modules.FactoryModule;
import ch.swisssmp.utils.YamlConfiguration;

public class NPCFactory {
	private String identifier;
	private String displayName;
	private boolean displayNameVisible;
	
	private EntityType entityType;
	private YamlConfiguration data;
	
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
	
	public void setYamlConfiguration(YamlConfiguration yamlConfiguration){
		this.data = yamlConfiguration;
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
			result.setYamlConfiguration(data);
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
