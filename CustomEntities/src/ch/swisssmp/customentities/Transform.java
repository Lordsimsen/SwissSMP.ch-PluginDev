package ch.swisssmp.customentities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.util.Vector;

public class Transform {
	private CustomEntity entity;
	private Transform parent;
	private Vector position;
	private Vector rotation;
	private List<Component> components = new ArrayList<Component>();
	private List<Transform> children = new ArrayList<Transform>();
	
	public CustomEntity getEntity(){
		return this.entity;
	}
	
	public Transform getParent(){
		return this.parent;
	}
	
	public void setParent(Transform parent){
		if(this.parent!=null) this.parent.children.remove(this);
		if(parent!=null) parent.children.add(this);
		this.parent = parent;
	}
	
	public Vector getLocalPosition(){
		return this.position;
	}
	
	public Vector getLocalRotation(){
		return this.rotation;
	}
	
	public Collection<Component> getComponents(){
		return new ArrayList<Component>(this.components);
	}
	
	public Collection<Transform> getChildren(){
		return new ArrayList<Transform>(this.children);
	}
	
	protected void update(){
		
	}
}
