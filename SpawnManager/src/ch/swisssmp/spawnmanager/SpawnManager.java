package ch.swisssmp.spawnmanager;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class SpawnManager extends JavaPlugin implements Listener{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static SpawnManager plugin;
	protected static boolean debug;
	private static Random random = new Random();
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		Bukkit.getPluginManager().registerEvents(this, this);
		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("spawn").setExecutor(playerCommand);
		this.getCommand("settemplespawn").setExecutor(playerCommand);
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll((JavaPlugin)this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onPlayerRespawn(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if(event.isBedSpawn()){
			return;
		}
		try {
			YamlConfiguration yamlConfiguration;
			yamlConfiguration = DataSource.getYamlResponse("players/spawn.php", new String[]{
					"player="+player.getUniqueId().toString(),
					"x="+(int)Math.round(player.getLocation().getX()),
					"y="+(int)Math.round(player.getLocation().getY()),
					"z="+(int)Math.round(player.getLocation().getZ()),
					"world="+URLEncoder.encode(player.getWorld().getName(), "utf-8"),
					"flag=respawn"
			});
			if(yamlConfiguration==null) return;
			if(yamlConfiguration.contains("spawnpoint")){
				Location location = yamlConfiguration.getLocation("spawnpoint");
				if(location!=null){
					event.setRespawnLocation(location);
				}
			}
			if(yamlConfiguration.contains("message")){
				player.sendMessage(yamlConfiguration.getString("message"));
			}
			if(yamlConfiguration.contains("actionbar") || yamlConfiguration.contains("effect") || yamlConfiguration.contains("sound")){
				Bukkit.getScheduler().runTaskLater(SpawnManager.plugin, new Runnable(){
					public void run(){
						if(yamlConfiguration.contains("actionbar")){
							SwissSMPler swisssmpler = SwissSMPler.get(player);
							swisssmpler.sendActionBar(yamlConfiguration.getString("actionbar"));
						}
						if(yamlConfiguration.contains("effect")){
							PotionEffect potionEffect = yamlConfiguration.getPotionEffect("effect");
							if(potionEffect!=null) player.addPotionEffect(potionEffect);
						}
						if(yamlConfiguration.contains("sound")){
							Sound sound = Sound.valueOf(yamlConfiguration.getString("sound"));
							if(sound!=null){
								player.getWorld().playSound(player.getLocation(), sound, 5f, 0.8f+random.nextFloat()*0.4f);
							}
						}
					}
				}, 1l);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
