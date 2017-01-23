package ch.swisssmp.dynamiterooms;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Main extends JavaPlugin implements Listener{
	public static Logger logger;
	public static Server server;
	public static PluginDescriptionFile pdfFile;
	public static Main plugin;
	public static WorldGuardPlugin worldguardplugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		server = getServer();
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		Plugin wg = server.getPluginManager().getPlugin("WorldGuard");
		if(!(wg instanceof WorldGuardPlugin)){
			logger.info("DynamiteRooms requires WorldGuard!");
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		worldguardplugin = (WorldGuardPlugin) wg;
		
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		server.getPluginManager().registerEvents(this, this);
		
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	@EventHandler
	public void onBlockBreak(EntityExplodeEvent event){
		List<Block> destroyed = event.blockList();
	    Iterator<Block> it = destroyed.iterator();
        World world = event.getLocation().getWorld();
        RegionManager regionManager = worldguardplugin.getRegionManager(world);
	    while (it.hasNext()) {
	        Block block = it.next();
	        Location location = block.getLocation();
	        ApplicableRegionSet regions = regionManager.getApplicableRegions(location);
	        boolean allowed = false;
	        for(ProtectedRegion region: regions.getRegions()){
	        	if(region.getFlag(DefaultFlag.TNT) == State.ALLOW)
	        		allowed = true;
	        }
	        if (!allowed){
	            it.remove();
	        }
	    }
	}
}