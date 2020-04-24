package ch.swisssmp.zones.zoneinfos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.zones.MemberRole;
import ch.swisssmp.zones.WorldGuardUtil;
import ch.swisssmp.zones.ZoneType;
import ch.swisssmp.zones.editor.ActionResult;
import ch.swisssmp.zones.editor.Edge;

public abstract class PolygonZoneInfo extends ZoneInfo {

	private final List<Location> points;
	
	protected PolygonZoneInfo(World world, String regionId, ZoneType zoneType, ConfigurationSection dataSection) {
		super(world, regionId, zoneType, dataSection);
		this.points = new ArrayList<Location>();
		ConfigurationSection pointsSection = dataSection.getConfigurationSection("points");
		if(pointsSection!=null){
			for(String key : pointsSection.getKeys(false)){
				Vector v = pointsSection.getVector(key);
				if(v==null) continue;
				points.add(new Location(world, v.getX(), v.getY(), v.getZ()));
			}
		}
	}

	protected PolygonZoneInfo(ZoneType zoneType) {
		super(zoneType);
		this.points = new ArrayList<Location>();
	}
	
	@Override
	public RegionType getRegionType(){
		return RegionType.POLYGON;
	}
	
	@Override
	public List<Location> getPoints(){
		return Collections.unmodifiableList(points);
	}
	
	public void setPoints(List<Location> points){
		this.points.clear();
		for(Location l : points){
			this.points.add(l);
		}
	}
	
	public void addPoint(Location location){
		if(points.size()<=2){
			this.points.add(location);
			return;
		}
		Edge closest = null;
		double closestDistance = Double.MAX_VALUE;
		for(int i = 0; i < points.size(); i++){
			Location a = points.get(i);
			Location b = (i+1)<points.size() ? points.get(i+1) : points.get(0);
			Edge edge = new Edge(a,b);
			double distance = edge.getDistanceSquared(location);
			if(distance>closestDistance-0.001) continue;
			closestDistance = distance;
			closest = edge;
		}
		if(closest!=null){
			int index = this.points.indexOf(closest.getB());
			this.points.add(index, location);
		}
		else{
			this.points.add(location);
		}
	}
	
	/**
	 * Entfernt den nächstgelegenen Punkt
	 * @param location - Punkt in der Nähe der Zone
	 */
	public void removePoint(Location location){
		Location nearest = this.getNearestLocation(location);
		if(nearest==null) return;
		this.points.remove(nearest);
	}
	
	@Override
	public ActionResult edit(Location location, Action action){
		if(action==Action.LEFT_CLICK_AIR || action==Action.LEFT_CLICK_BLOCK){
			this.addPoint(location);
			return ActionResult.POINT_ADDED;
		}
		else if(action==Action.RIGHT_CLICK_AIR || action==Action.RIGHT_CLICK_BLOCK){
			if(this.points.size()==0) return ActionResult.NONE;
			this.removePoint(location);
			return ActionResult.POINT_REMOVED;
		}
		else return ActionResult.NONE;
	}
	
	@Override
	public ProtectedRegion createRegion(){
		//remove old region
		List<ProtectedRegion> children = new ArrayList<ProtectedRegion>();
		RegionManager manager = WorldGuardUtil.getManager(getWorld());
		if(manager==null) return null;
		ProtectedRegion existing = manager.getRegion(this.getId());
		if(existing!=null){
			for(ProtectedRegion other : manager.getRegions().values()){
				if(!existing.equals(other.getParent())) continue;
				children.add(other);
			}
			manager.removeRegion(existing.getId(), RemovalStrategy.UNSET_PARENT_IN_CHILDREN);
		}
		//create new region
		ProtectedRegion region = createRegion(getId(), getRegionType(), this.points, getMembers());
		if(region!=null){
			//add new region
			manager.addRegion(region);
			//link the children again
			for(ProtectedRegion child : children){
				try {
					child.setParent(region);
				} catch (CircularInheritanceException e) {
					e.printStackTrace();
				}
			}
		}
		return region;
	}
	
	private static ProtectedRegion createRegion(String regionId, RegionType type, List<Location> locations, Map<UUID,MemberRole> members){
		ProtectedRegion region = createPolygonRegion(regionId, locations);
		applyMembers(region, members);
		return region;
	}
	
	private static ProtectedRegion createPolygonRegion(String regionId, List<Location> locations){
		if(locations!=null && locations.size()<3) return null;
		List<BlockVector2> points = new ArrayList<BlockVector2>();
		for(Location location : locations){
			points.add(BlockVector2.at(location.getX(),location.getZ()));
		}
		return new ProtectedPolygonalRegion(regionId, points, 50, 256);
	}
	
	@Override
	public ItemStack getCost(Action action){
		if(action==Action.LEFT_CLICK_AIR || action==Action.LEFT_CLICK_BLOCK){
			return new ItemStack(Material.REDSTONE, 1);
		}
		return null;
	}
	
	@Override
	public void save(ConfigurationSection dataSection){
		super.save(dataSection);
		ConfigurationSection pointsSection = dataSection.createSection("points");
		int index = 0;
		for(Location location : points){
			pointsSection.set("p"+index, location.toVector());
			index++;
		}
	}
	
	@Override
	public ZoneSnapshot getSnapshot(){
		return new ZoneSnapshot(points);
	}
	
	@Override
	public void applySnapshot(ZoneSnapshot snapshot){
		this.setPoints(snapshot.getPoints());
	}
	
	private Location getNearestLocation(Location location){
		if(this.points.size()==0 || location.getWorld()!=this.getWorld()) return null;
		Location nearest = this.points.get(0);
		double nearestDistance = nearest.distanceSquared(location);
		for(Location point : this.points){
			double distance = point.distanceSquared(location);
			if(distance>nearestDistance) continue;
			nearest = point;
			nearestDistance = distance;
		}
		return nearest;
	}
	
	/*
	private void recalculateShape(){
		List<Vector> shape = GrahamScan.calculate(this.points.stream().map(p->p.toVector()).collect(Collectors.toList()));
		this.points.clear();
		for(Vector v : shape){
			points.add(new Location(this.getWorld(), v.getX(), v.getY(), v.getZ()));
		}
	}
	*/
}
