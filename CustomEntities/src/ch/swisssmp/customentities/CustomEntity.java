package ch.swisssmp.customentities;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.entity.Entity;

public class CustomEntity {
	private static HashMap<Entity,CustomEntity> entities = new HashMap<Entity,CustomEntity>();
	
	private Entity entity;
	private Transform transform;
	
	protected void Update(){
		this.transform.update();
	}
	
	protected void OnDeath(){
		entities.remove(this.entity);
	}
	
	public static CustomEntity get(Entity entity){
		return entities.get(entity);
	}
	
	protected static Collection<CustomEntity> getAll(){
		return entities.values();
	}
}
