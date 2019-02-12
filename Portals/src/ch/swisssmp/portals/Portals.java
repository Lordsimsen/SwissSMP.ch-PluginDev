package ch.swisssmp.portals;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public class Portals extends JavaPlugin implements Listener{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static Portals plugin;
	protected static boolean debug;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll((JavaPlugin)this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onRegionEnter(RegionEnterEvent event){
		ProtectedRegion region = event.getRegion();
		String regionName = region.getId().toLowerCase();
		if(!regionName.startsWith("portal_")){
			return;
		}
		Player player = event.getPlayer();
		HTTPRequest request = DataSource.getResponse(plugin, "portal.php", new String[]{
			"portal="+regionName,
			"player="+player.getUniqueId().toString()
		});
		request.onFinish(()->{
			onPortalEnter(request.getYamlResponse(), player);
		});
	}
	
	private void onPortalEnter(YamlConfiguration yamlConfiguration, Player player){
		if(yamlConfiguration==null) return;
		if(yamlConfiguration.contains("message")){
			player.sendMessage(yamlConfiguration.getString("message"));
		}
		if(yamlConfiguration.contains("destination")){
			Location destination = yamlConfiguration.getLocation("destination");
			if(destination==null){
				Bukkit.getLogger().info("Couldn't teleport "+player.getName()+" to an unkown location!");
				ConfigurationSection destinationSection = yamlConfiguration.getConfigurationSection("destination");
				for(String key : destinationSection.getKeys(false)){
					Bukkit.getLogger().info(key+": "+destinationSection.getString(key));
				}
				return;
			}
			player.teleport(destination);
			player.playSound(destination, Sound.BLOCK_PORTAL_TRAVEL, 5, 1);
		}
	}
}
