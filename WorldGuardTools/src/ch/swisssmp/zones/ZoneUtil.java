package ch.swisssmp.zones;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.zones.zoneinfos.ZoneInfo;
import ch.swisssmp.zones.zoneinfos.ZoneInfoState;

public class ZoneUtil {
	
	public static String toRegionName(String name){
		String result = name.toLowerCase();
		final String[] replace = new String[]{" ","ö","ä","ü"};
		final String[] replaceWith = new String[]{"_","oe","ae","ue"};
		for(int i = 0; i < replace.length; i++){
			result = result.replace(replace[i], replaceWith[i]);
		}
		return result.replaceAll("[^a-z0-9_]", "");
	}
	
	protected static YamlConfiguration getZonesData(World world){
		File file = FileUtil.getZonesFile(world);
		if(!file.exists()) return null;
		return YamlConfiguration.loadConfiguration(file);
	}
	
	protected static void saveZonesData(World world, YamlConfiguration yamlConfiguration){
		File file = FileUtil.getZonesFile(world);
		yamlConfiguration.save(file);
	}
	
	public static ConfigurationSection getRegionData(World world, String regionId){
		YamlConfiguration yamlConfiguration = getZonesData(world);
		if(yamlConfiguration==null || !yamlConfiguration.contains("regions")){
			return null;
		}
		ConfigurationSection regionsSection = yamlConfiguration.getConfigurationSection("regions");
		if(!regionsSection.contains(regionId)){
			return null;
		}
		return regionsSection.getConfigurationSection(regionId);
	}
	
	public static void updateZoneInfos(Inventory inventory){
		for(ItemStack itemStack : inventory){
			if(itemStack==null) continue;
			ZoneInfo zoneInfo = ZoneInfo.get(itemStack);
			if(zoneInfo==null) continue;
			if(zoneInfo.getState()!=ZoneInfoState.MISSING){
				zoneInfo.apply(itemStack);
				continue;
			}
			ZoneInfo.clear(itemStack);
		}
	}
	
	protected static List<Location> loadLocations(World world, ProtectedRegion region){
		List<Location> locations = new ArrayList<Location>();
		if(region==null) return locations;
		if(region.getType()==RegionType.CUBOID){
			BlockVector3 min = region.getMinimumPoint();
			BlockVector3 max = region.getMaximumPoint();
			locations.add(new Location(world, min.getBlockX(), min.getBlockY(), min.getBlockZ()));
			locations.add(new Location(world, max.getBlockX(), max.getBlockY(), max.getBlockZ()));
		}
		else{
			ConfigurationSection regionSection = getRegionData(world, region.getId());
			if(regionSection==null || !regionSection.contains("points")){
				return getWorldGuardPoints(world, region);
			}
			ConfigurationSection pointsSection = regionSection.getConfigurationSection("points");
			for(String key : pointsSection.getKeys(false)){
				Location location = pointsSection.getLocation(key,world);
				if(location==null){
					continue;
				}
				locations.add(location);
			}
		}
		return locations;
	}
	
	private static List<Location> getWorldGuardPoints(World world, ProtectedRegion region){
		List<Location> locations = new ArrayList<Location>();
		for(BlockVector2 point : region.getPoints()){
			locations.add(new Location(world, point.getX(), 64, point.getZ()));
		}
		return locations;
	}
	
	protected static List<Edge> buildPolygonEdges(List<Location> locations){
		List<Edge> edges = new ArrayList<Edge>();
		for(int i = 0; i < locations.size()-1; i++){
			Edge edge = new Edge(locations.get(i), locations.get(i+1));
			edges.add(edge);
		}
		if(locations.size()>2){
			Edge edge = new Edge(locations.get(locations.size()-1), locations.get(0));
			edges.add(edge);
		}
		return edges;
	}
	
	protected static List<Edge> buildBoxEdges(Location a, Location b){
		World world = a.getWorld();
		double minX = a.getX();
		double minY = a.getY();
		double minZ = a.getZ();
		double maxX = b.getX();
		double maxY = b.getY();
		double maxZ = b.getZ();
		Location LA = new Location(world, minX, minY, minZ);
		Location LB = new Location(world, maxX, minY, minZ);
		Location LC = new Location(world, maxX, minY, maxZ);
		Location LD = new Location(world, minX, minY, maxZ);
		Location UA = new Location(world, minX, maxY, minZ);
		Location UB = new Location(world, maxX, maxY, minZ);
		Location UC = new Location(world, maxX, maxY, maxZ);
		Location UD = new Location(world, minX, maxY, maxZ);
		List<Edge> edges = new ArrayList<Edge>();
		//X
		edges.add(new Edge(LA,LB));
		edges.add(new Edge(LC,LD));
		edges.add(new Edge(UA,UB));
		edges.add(new Edge(UC,UD));
		//Z
		edges.add(new Edge(LA,LD));
		edges.add(new Edge(LB,LC));
		edges.add(new Edge(UA,UD));
		edges.add(new Edge(UB,UC));
		//Y
		edges.add(new Edge(LA,UA));
		edges.add(new Edge(LB,UB));
		edges.add(new Edge(LC,UC));
		edges.add(new Edge(LD,UD));
		
		return edges;
	}
	
	/*
	protected static void saveLocations(World world, ZoneType zoneType, ProtectedRegion region, List<Location> locations){
		YamlConfiguration yamlConfiguration = getZonesData(world);
		if(yamlConfiguration==null) yamlConfiguration = new YamlConfiguration();
		ConfigurationSection regionsSection;
		if(!yamlConfiguration.contains("regions")) regionsSection = yamlConfiguration.createSection("regions");
		else regionsSection = yamlConfiguration.getConfigurationSection("regions");
		ConfigurationSection regionSection;
		if(!regionsSection.contains(region.getId())) regionSection = regionsSection.createSection(region.getId());
		else regionSection = regionsSection.getConfigurationSection(region.getId());
		regionSection.set("zone_type", zoneType.toString());
		ConfigurationSection pointsSection;
		if(!regionSection.contains("points")) pointsSection = regionSection.createSection("points");
		else pointsSection = regionSection.getConfigurationSection("points");
		int index = 0;
		for(Location location : locations){
			ConfigurationSection pointSection = pointsSection.createSection("point_"+index);
			pointSection.set("x", location.getX());
			pointSection.set("y", location.getY());
			pointSection.set("z", location.getZ());
			index++;
		}
		File file = FileUtil.getRegionsFile(world);
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		yamlConfiguration.save(file);
	}
	*/
}
