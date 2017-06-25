package ch.swisssmp.worldguardmanager;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.RequestMethod;

public class WorldGuardManager extends JavaPlugin{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static WorldGuardManager plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		
		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("worldguardmanager").setExecutor(playerCommand);
		
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		Plugin worldguardplugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if(plugin == null || !(worldguardplugin instanceof WorldGuardPlugin)){
			logger.info("WorldGuard not found, WorldGuardManager exiting...");
			onDisable();
		}
		UpdateWorldGuardInfos();
	}
	
	public void UpdateWorldGuardInfos(){
			String encoding = "UTF-8";

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
				try{
					worldName = URLEncoder.encode(world.getName(), encoding);
					if(worldName.contains("instance")) continue;
					informations = new ArrayList<String>();
					informations.add("world="+worldName);
					regionManager = WorldGuardPlugin.inst().getRegionManager(world);
					regions = regionManager.getRegions();
					for(Entry<String,ProtectedRegion> regionEntry : regions.entrySet()){
						regionName = URLEncoder.encode(regionEntry.getKey(), encoding);
						region = regionEntry.getValue();
						informations.add("regions["+regionName+"][id]="+URLEncoder.encode(region.getId(), encoding));
						informations.add("regions["+regionName+"][priority]="+URLEncoder.encode(String.valueOf(region.getPriority()), encoding));
						informations.add("regions["+regionName+"][type]="+URLEncoder.encode(region.getType().toString(), encoding));
						informations.add("regions["+regionName+"][minimum]="+URLEncoder.encode(region.getMinimumPoint().toString(), encoding));
						informations.add("regions["+regionName+"][maximum]="+URLEncoder.encode(region.getMaximumPoint().toString(), encoding));
						if(region.getParent()!=null){
							informations.add("regions["+regionName+"][parent]="+URLEncoder.encode(region.getParent().getId(), encoding));
						}
						for(Entry<Flag<?>, Object> flagEntry : region.getFlags().entrySet()){
							flag = flagEntry.getKey();
							flagValue = flagEntry.getValue();
							informations.add("regions["+regionName+"][flags]["+flag.getName()+"][value]="+URLEncoder.encode(String.valueOf(flagValue),encoding));
							informations.add("regions["+regionName+"][flags]["+flag.getName()+"][inherited]="+URLEncoder.encode(String.valueOf(flag.implicitlySetWithMembership()),encoding));
						}
					}
					
					infoArray = new String[informations.size()];
					
					DataSource.getResponse("world/region_info.php", informations.toArray(infoArray), RequestMethod.POST);
					logger.info("Region info for "+worldName+" updated.");
				}
				catch(Exception e){
					logger.info("[ServerManager] Error updating region info for "+world.getName());
					e.printStackTrace();
				}
			}
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
}
