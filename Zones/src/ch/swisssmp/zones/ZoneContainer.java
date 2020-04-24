package ch.swisssmp.zones;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.zones.zoneinfos.ZoneInfo;

public class ZoneContainer {
	
	private final World world;
	private final HashMap<String,ZoneInfo> zones = new HashMap<String,ZoneInfo>();
	
	private ZoneContainer(World world){
		this.world = world;
	}
	
	public World getWorld(){
		return world;
	}
	
	public Collection<ZoneInfo> getZones(){
		return zones.values();
	}
	
	public void add(ZoneInfo zoneInfo){
		String id = zoneInfo.getId();
		if(id==null) return;
		zones.put(id, zoneInfo);
	}
	
	public void remove(ZoneInfo zoneInfo){
		zones.remove(zoneInfo.getId());
	}
	
	public ZoneInfo getZone(String zoneId){
		return zones.get(zoneId);
	}
	
	public List<ZoneInfo> getZones(Player player){
		return zones.values().stream()
				.filter(z->(z.getMembers().containsKey(player.getUniqueId()) || (player.isOp() && player.getGameMode()==GameMode.CREATIVE)))
				.collect(Collectors.toList());
	}
	
	private void load(){
		YamlConfiguration yamlConfiguration = ZoneUtil.getZonesData(world);
		if(yamlConfiguration==null || !yamlConfiguration.contains("zones")) return;
		ConfigurationSection zonesSection = yamlConfiguration.getConfigurationSection("zones");
		for(String key : zonesSection.getKeys(false)){
			ConfigurationSection zoneSection = zonesSection.getConfigurationSection(key);
			ZoneInfo zoneInfo = ZoneInfo.load(world, key, zoneSection);
			if(zoneInfo==null) continue;
			zones.put(key, zoneInfo);
		}
	}
	
	public void save(){
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		ConfigurationSection zonesSection = yamlConfiguration.createSection("zones");
		for(ZoneInfo zoneInfo : getZones()){
			ConfigurationSection zoneSection = zonesSection.createSection(zoneInfo.getId());
			zoneInfo.save(zoneSection);
		}
		ZoneUtil.saveZonesData(world, yamlConfiguration);
		
		try {
			RegionManager manager = WorldGuardUtil.getManager(world);
			manager.save();
		} catch (StorageException e) {
			e.printStackTrace();
		} catch(NoClassDefFoundError e){
			//do nothing
		}
	}
	
	protected static ZoneContainer load(World world){
		ZoneContainer result = new ZoneContainer(world);
		result.load();
		ZoneContainers.add(world, result);
		return result;
	}
	
	protected static void unload(World world){
		ZoneContainers.remove(world);
	}
	
	public static ZoneContainer get(World world){
		return ZoneContainers.get(world);
	}
}
