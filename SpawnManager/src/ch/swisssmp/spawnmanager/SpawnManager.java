package ch.swisssmp.spawnmanager;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class SpawnManager extends JavaPlugin implements Listener{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static SpawnManager plugin;
	protected static boolean debug;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		Bukkit.getPluginManager().registerEvents(this, this);
		this.getCommand("spawn").setExecutor(new PlayerCommand());
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll((JavaPlugin)this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onPlayerDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		if(player.getBedSpawnLocation()!=null){
			return;
		}
		try {
			YamlConfiguration yamlConfiguration;
			yamlConfiguration = DataSource.getYamlResponse("players/spawn.php", new String[]{
					"player="+player.getUniqueId().toString(),
					"world="+URLEncoder.encode(player.getWorld().getName(), "utf-8")
			});
			if(yamlConfiguration==null) return;
			Location location = yamlConfiguration.getLocation("spawnpoint");
			if(location!=null){
				player.setBedSpawnLocation(location, true);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
