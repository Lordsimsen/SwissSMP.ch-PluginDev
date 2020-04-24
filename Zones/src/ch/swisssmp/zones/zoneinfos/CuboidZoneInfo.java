package ch.swisssmp.zones.zoneinfos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.zones.MemberRole;
import ch.swisssmp.zones.WorldGuardUtil;
import ch.swisssmp.zones.ZoneType;
import ch.swisssmp.zones.editor.ActionResult;

public abstract class CuboidZoneInfo extends ZoneInfo {

	private Block min;
	private Block max;
	
	protected CuboidZoneInfo(World world, String regionId, ZoneType zoneType, ConfigurationSection dataSection) {
		super(world, regionId, zoneType, dataSection);

	}

	protected CuboidZoneInfo(ZoneType zoneType) {
		super(zoneType);
		
	}
	
	public Block getMin(){
		return min;
	}
	
	public Block getMax(){
		return max;
	}
	
	public void set(BlockVector min, BlockVector max){
		setMin(min);
		setMax(max);
	}
	
	public void setMin(BlockVector block){
		min = getWorld().getBlockAt(block.getBlockX(), block.getBlockY(), block.getBlockZ());
		if(max==null) max = min;
		recalculateMinMax();
	}
	
	public void setMax(BlockVector block){
		max = getWorld().getBlockAt(block.getBlockX(), block.getBlockY(), block.getBlockZ());
		if(min==null) min = max;
		recalculateMinMax();
	}
	
	private void recalculateMinMax(){
		World world = min.getWorld();
		int minX = Math.min(min.getX(), max.getX());
		int minY = Math.min(min.getY(), max.getY());
		int minZ = Math.min(min.getZ(), max.getZ());
		int maxX = Math.max(min.getX(), max.getX());
		int maxY = Math.max(min.getY(), max.getY());
		int maxZ = Math.max(min.getZ(), max.getZ());
		min = world.getBlockAt(minX, minY, minZ);
		max = world.getBlockAt(maxX, maxY, maxZ);
		
	}
	
	@Override
	public ActionResult edit(Location location, Action action){
		Block block = location.getBlock();
		BlockVector blockVector = new BlockVector(block.getX(), block.getY(), block.getZ());
		if(action==Action.LEFT_CLICK_AIR || action==Action.LEFT_CLICK_BLOCK){
			boolean hadMin = min!=null;
			this.setMin(blockVector);
			return hadMin ? ActionResult.ZONE_ADJUSTED : ActionResult.POINT_ADDED;
		}
		else if(action==Action.RIGHT_CLICK_AIR || action==Action.RIGHT_CLICK_BLOCK){
			boolean hadMax = max!=null;
			this.setMax(blockVector);
			return hadMax ? ActionResult.ZONE_ADJUSTED : ActionResult.POINT_ADDED;
		}
		return ActionResult.NONE;
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
		ProtectedRegion region = createRegion(getId(), getRegionType(), min, max, getMembers());
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
	
	@Override
	public ItemStack getCost(Action action){
		return null;
	}
	
	@Override
	public RegionType getRegionType(){
		return RegionType.CUBOID;
	}
	
	@Override
	public List<Location> getPoints(){
		Location min = this.min!=null ? this.min.getLocation().add(0.5,0.5,0.5) : new Location(this.getWorld(),0,0,0);
		Location max = this.min!=null ? this.max.getLocation().add(0.5,0.5,0.5) : new Location(this.getWorld(),0,0,0);
		return Arrays.asList(min, max);
	}
	
	@Override
	public void save(ConfigurationSection dataSection){
		super.save(dataSection);
		if(min!=null) saveBlock(dataSection, "min", min);
		if(max!=null) saveBlock(dataSection, "max", max);
	}
	
	@Override
	public ZoneSnapshot getSnapshot(){
		return new ZoneSnapshot(Arrays.asList(min.getLocation(),max.getLocation()));
	}
	
	@Override
	public void applySnapshot(ZoneSnapshot snapshot){
		List<Location> locations = snapshot.getPoints();
		if(locations.size()>0){
			min = locations.get(0).getBlock();
		}
		if(locations.size()>1){
			max = locations.get(1).getBlock();
		}
	}
	
	private ProtectedRegion createRegion(String regionId, RegionType type, Block min, Block max, Map<UUID,MemberRole> members){
		if(min==null || max==null) return null;
		ProtectedRegion region = createCuboidRegion(regionId, min.getLocation(), max.getLocation().add(1,1,1));
		applyMembers(region, members);
		return region;
	}
	
	private static ProtectedRegion createCuboidRegion(String regionId, Location min, Location max){
		BlockVector3 a = BlockVector3.at(min.getX(), min.getY(), min.getZ());
		BlockVector3 b = BlockVector3.at(max.getX(), max.getY(), max.getZ());
		return new ProtectedCuboidRegion(regionId, a, b);
	}
}
