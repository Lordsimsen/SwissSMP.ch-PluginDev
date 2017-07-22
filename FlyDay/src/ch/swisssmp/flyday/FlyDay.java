package ch.swisssmp.flyday;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class FlyDay extends JavaPlugin{

	public static FlyDay plugin;
	public static Logger logger;
	public static Server server;
	public static PluginDescriptionFile pdfFile;
	
	private static List<String> permittedWorlds = new ArrayList<String>();
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		FlyDayCommand flyDayCommand = new FlyDayCommand();
		this.getCommand("FlyDay").setExecutor(flyDayCommand);
		
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		updateState();
	}
	
	public static boolean isFlightPermitted(World world){
		return isFlightPermitted(world.getName());
	}
	
	public static boolean isFlightPermitted(String worldName){
		return permittedWorlds.contains(worldName);
	}
	
	public static void updateState(){
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("flyday/isnow.php");
		if(yamlConfiguration==null || !yamlConfiguration.contains("global_flight")){
			permittedWorlds.clear();
		}
		else{
			permittedWorlds = yamlConfiguration.getStringList("global_flight");
		}
		updatePlayers();
	}
	
	public static void updatePlayers(){
		for(Player player : Bukkit.getOnlinePlayers()){
			updatePlayer(player);
		}
	}
	
	public static void updatePlayer(Player player){
		updatePlayer(player, true);
	}
	
	public static void updatePlayer(Player player, boolean gracePeriod){
		if(player==null) return;
		if(player.hasPermission("flyday.bypass")) return;
		boolean flightPermitted = isFlightPermitted(player.getWorld());
		if(flightPermitted==player.getAllowFlight()) return;
		if(!player.isOnGround() && !flightPermitted && gracePeriod){
			player.sendMessage("[§cWarnung§r] §cDeine Flugrechte werden in 30 Sekunden deaktiviert.");
			Bukkit.getScheduler().runTaskLater(FlyDay.plugin, new Runnable(){
				public void run(){
					updatePlayer(player, false);
				}
			}, 30*20L);
		}
		else{
			setFlightPermissions(player, flightPermitted);
		}
	}
	
	private static void setFlightPermissions(Player player, boolean flightPermitted){
		player.setAllowFlight(flightPermitted);
		player.setFlying(flightPermitted);
		if(flightPermitted){
			player.sendMessage("[§EFlyDay§r] §aFlug-Rechte aktiviert.");
		}
		else{
			player.sendMessage("[§EFlyDay§r] §cFlug-Rechte deaktiviert.");
		}
	}
    
	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
}
