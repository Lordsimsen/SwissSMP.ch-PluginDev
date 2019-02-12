package ch.swisssmp.worldguardmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.RequestMethod;

public class WorldGuardManager extends JavaPlugin{
	private static PluginDescriptionFile pdfFile;
	private static WorldGuardManager plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		
		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("worldguardmanager").setExecutor(playerCommand);
		
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		Plugin worldguardplugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if(plugin == null || !(worldguardplugin instanceof WorldGuardPlugin)){
			Bukkit.getLogger().info("WorldGuard not found, WorldGuardManager exiting...");
			onDisable();
		}
		updateWorldGuardInfos();
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static RegionManager getRegionManager(World world){
		WorldGuardPlatform platform = WorldGuard.getInstance().getPlatform();
		com.sk89q.worldedit.world.World worldEditWorld = platform.getWorldByName(world.getName());
		RegionContainer container = platform.getRegionContainer();
		RegionManager regionManager = container.get(worldEditWorld);
		return regionManager;
	}
	
	public static void updateWorldGuardInfos(){
			List<String> informations;
			
			List<World> worlds = Bukkit.getWorlds();
			String worldName;
			
			RegionManager regionManager;
			Map<String,ProtectedRegion> regions;
			String regionName;
			ProtectedRegion region;
			
			Flag<?> flag;
			Object flagValue;
			
			String[] infoArray;
			
			for(World world : worlds){
				worldName = URLEncoder.encode(world.getName());
				if(worldName.contains("instance")) continue;
				informations = new ArrayList<String>();
				informations.add("world="+worldName);
				regionManager = WorldGuardManager.getRegionManager(world);
				regions = regionManager.getRegions();
				for(Entry<String,ProtectedRegion> regionEntry : regions.entrySet()){
					regionName = URLEncoder.encode(regionEntry.getKey());
					region = regionEntry.getValue();
					informations.add("regions["+regionName+"][id]="+URLEncoder.encode(region.getId()));
					informations.add("regions["+regionName+"][priority]="+URLEncoder.encode(String.valueOf(region.getPriority())));
					informations.add("regions["+regionName+"][type]="+URLEncoder.encode(region.getType().toString()));
					informations.add("regions["+regionName+"][minimum]="+URLEncoder.encode(region.getMinimumPoint().toString()));
					informations.add("regions["+regionName+"][maximum]="+URLEncoder.encode(region.getMaximumPoint().toString()));
					if(region.getParent()!=null){
						informations.add("regions["+regionName+"][parent]="+URLEncoder.encode(region.getParent().getId()));
					}
					for(Entry<Flag<?>, Object> flagEntry : region.getFlags().entrySet()){
						flag = flagEntry.getKey();
						flagValue = flagEntry.getValue();
						informations.add("regions["+regionName+"][flags]["+flag.getName()+"][value]="+URLEncoder.encode(String.valueOf(flagValue)));
						informations.add("regions["+regionName+"][flags]["+flag.getName()+"][inherited]="+URLEncoder.encode(String.valueOf(flag.implicitlySetWithMembership())));
					}
				}
				
				infoArray = new String[informations.size()];
				informations.toArray(infoArray);
				DataSource.getResponse(WorldGuardManager.getInstance(), "region_info.php", infoArray, RequestMethod.POST);
				Bukkit.getLogger().info("[WorldGuardManager] Region info for "+worldName+" updated.");
			}
	}
	
	public static WorldGuardManager getInstance(){
		return plugin;
	}
}
