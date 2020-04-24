package ch.swisssmp.sculptures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.Vector;

public abstract class SculpturePart {
	private final ArmorStand armorStand;
	private Quaternion pose;

	private SculpturePart parent;
	private Vector3 anchorPosition;
	private Quaternion anchorRotation;
	
	private final HashMap<String,SculpturePart> children = new HashMap<String,SculpturePart>();
	
	protected SculpturePart(ArmorStand armorStand, Quaternion pose) {
		this.armorStand = armorStand;
		this.pose = pose;
	}	
	
	public SculpturePart getParent() {
		return parent;
	}
	
	public ArmorStand getArmorStand() {
		return armorStand;
	}
	
	public Quaternion getPose() {
		return pose.clone();
	}
	
	private Optional<String> getRelation(SculpturePart part){
		return children.entrySet().stream().filter(e->e.getValue()==part).map(e->e.getKey()).findAny();
	}
	
	public void setPose(Quaternion pose) {
		this.pose = pose;
		this.recalculateState();
	}
	
	public void setParent(String id, SculpturePart parent, Vector anchorPosition, Quaternion anchorRotation) throws Exception {
		if(this.parent!=null) {
			this.parent.removeChild(this);
		}
		parent.addChild(id, this);
		this.parent = parent;
		this.anchorPosition = new Vector3(anchorPosition);
		this.anchorRotation = anchorRotation;
		this.recalculateState();
	}
	
	public void clearParent() {
		if(this.parent!=null) {
			this.parent.removeChild(this);
		}
	}
	
	private void addChild(String id, SculpturePart part) throws Exception {
		if(this.children.containsKey(id)) {
			throw new Exception("This key already exists!");
		}
		this.children.put(id, part);
	}
	
	private void removeChild(SculpturePart part) {
		Optional<String> id = getRelation(part);
		if(!id.isPresent()) return;
		removeChild(id.get());
	}
	
	private void removeChild(String id) {
		if(!children.containsKey(id)) {
			return;
		}
		SculpturePart p = children.get(id);
		p.parent = null;
		p.recalculateState();
		children.remove(id);
	}
	
	public void recalculateState() {
		Quaternion pose = this.pose;
		if(parent!=null) {
			Location location = getLocation(anchorPosition);
			armorStand.teleport(location);
			pose = parent.getPose().mulThis(pose);
		}
		this.pose = pose;
		this.recalculatePose(pose);
		for(Entry<String,SculpturePart> r : children.entrySet()) {
			r.getValue().recalculateState();
		}
	}
	
	protected abstract void recalculatePose(Quaternion anchorRotation);
	
	public Location getLocation() {
		return armorStand.getLocation();
	}
	
	private Location getLocation(Vector3 localPosition) {
		Location parentLocation = parent.getLocation();
		World world = parentLocation.getWorld();
		Quaternion parentRotation = parent.getPose();
		Vector3 worldPosition = parentRotation.multiply(localPosition);
		return new Location(world, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
	}
}
