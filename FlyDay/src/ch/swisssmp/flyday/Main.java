package ch.swisssmp.flyday;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{

	public static Main plugin;
	public static Logger logger;
	public static Server server;
	public static PluginDescriptionFile pdfFile;
	private static YamlConfiguration yamlConfiguration = new YamlConfiguration();
	private static File configFile;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		FlyDayCommand flyDayCommand = new FlyDayCommand();
		this.getCommand("FlyDay").setExecutor(flyDayCommand);
		
		configFile = new File(this.getDataFolder(), "config.yml");
		if(!configFile.exists()){
        	configFile.getParentFile().mkdirs();
            copy(getResource("config.yml"), configFile);
		}
		try {
			yamlConfiguration.load(configFile);
		} catch (IOException | InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onLogin(PlayerLoginEvent event){
		if(!isFlyDay()){
			return;
		}
		Player player = event.getPlayer();
		if(!player.hasPermission("flyday.participate")) return;
		if(player.hasPermission(getPermission())){
			Bukkit.getScheduler().runTaskLater(this, new Runnable(){
				@Override
				public void run() {
					String command = getCommandString(player, false);
					if(command==null) return;
					command = command.replace("{player}", player.getName());
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
				}}, 5L);
		}
		else{
			Bukkit.getScheduler().runTaskLater(this, new Runnable(){
				@Override
				public void run() {
					String command = getCommandString(player, true);
					if(command==null) return;
					command = command.replace("{player}", player.getName());
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
				}}, 5L);
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onTeleport(PlayerTeleportEvent event){
		Player player = event.getPlayer();
		if(!player.hasPermission("flyday.participate")) return;
		if(!isFlyDay()){
			return;
		}
		Location location = event.getTo();
		List<World> worlds = getPermittedWorlds();
		if(worlds.contains(location.getWorld())){
			//fly is permitted, give fly permission
			String command = getCommandString(player, true);
			if(command==null) return;
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
		}
		else{
			//fly is not permitted, deny teleport if player has flight permission
			if(player.hasPermission(getPermission())){
				event.setCancelled(true);
			}
		}
	}
	
	public static String getPermission(){
		return yamlConfiguration.getString("permission");
	}
	
	public static String getFuckingMainCommand(){
		return yamlConfiguration.getString("command");
	}
	public static String getFuckingReverseCommand(){
		return yamlConfiguration.getString("reverse_command");
	}
	
	public static String getCommandString(Player player, boolean useMainCommand){
		String command;
		if(useMainCommand){
			command = getFuckingMainCommand();
		}
		else{
			command = getFuckingReverseCommand();
		}
		if(command==null) return null;
		command = command.replace("{player}", player.getName());
		String permission = getPermission();
		if(permission!=null){
			command = command.replace("{permission}", permission);
		}
		return command;
	}
	
	public static boolean isFlyDay(){
		return yamlConfiguration.getBoolean("flyday");
	}
	public static void setFlyDay(boolean flyDay){
		yamlConfiguration.set("flyday", flyDay);
		editPlayers();
	}
	private static void editPlayers(){
		List<World> worlds = getPermittedWorlds();
		boolean flyDay = isFlyDay();
		for(World world : Bukkit.getWorlds()){
			List<Player> players = world.getPlayers();
			for(Player player : players){
				if(flyDay && worlds.contains(world)){
					String commandString = getCommandString(player, true);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandString);
				}
				else{
					String commandString = getCommandString(player, false);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandString);
				}
			}
		}
	}
	public static List<World> getPermittedWorlds(){
		List<String> worldNames = yamlConfiguration.getStringList("permitted_worlds");
		List<World> result = new ArrayList<World>();
		if(worldNames==null){
			return result;
		}
		for(String worldName : worldNames){
			World world = Bukkit.getWorld(worldName);
			if(world!=null) result.add(world);
		}
		return result;
	}
    
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
