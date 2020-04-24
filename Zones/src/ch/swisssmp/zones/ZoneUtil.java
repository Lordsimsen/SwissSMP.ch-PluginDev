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
