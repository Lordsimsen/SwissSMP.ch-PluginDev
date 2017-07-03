package ch.swisssmp.groupannouncer;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class GroupAnnouncer extends JavaPlugin{
	protected static Logger logger;
	protected static File configFile;
	protected static YamlConfiguration config;
	protected static PluginDescriptionFile pdfFile;

	protected static File dataFolder;
	protected static GroupAnnouncer plugin;
	protected static boolean debug;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		this.getCommand("groupannouncer").setExecutor(new PlayerCommand());
	}
	
	public static ArrayList<String> announce(String message){
		return announce(message, null);
	}
	
	public static ArrayList<String> announce(String message, String filter){
		int count;
		ArrayList<String> counters = new ArrayList<String>();
		if(filter==null || filter.isEmpty() || filter.equals("-")){
			count = 0;
			for(Player player : Bukkit.getOnlinePlayers()){
				player.sendMessage(message);
				count++;
			}
			counters.add(count+"x");
		}
		else{
			boolean exclusion = false;
			if(filter.startsWith("-")){
				exclusion = true;
				filter = filter.substring(1);
			}
			String[] groups = filter.toLowerCase().split(",");
			if(exclusion){
				count = 0;
				outerloop:
				for(Player player : Bukkit.getOnlinePlayers()){
					for(String group : groups){
						if(player.hasPermission("ga."+group)){
							logger.info(player.getName()+" has ga."+group);
							continue outerloop;
						}
					}
					logger.info(String.valueOf(player.hasPermission("ga.ressortleiter")));
					player.sendMessage(message);
					count++;
				}
				counters.add(count+"x");
			}
			else{
				ArrayList<UUID> sent = new ArrayList<UUID>();
				for(String group : groups){
					count = 0;
					for(Player player : Bukkit.getOnlinePlayers()){
						if(sent.contains(player.getUniqueId())) continue;
						if((player.hasPermission("ga."+group))){
							player.sendMessage(message);
							sent.add(player.getUniqueId());
							count++;
						}
						else{
							logger.info(player.getName()+" is missing usergroup."+group);
						}
					}
					counters.add(group +" "+count+"x");
				}
			}
		}
		return counters;
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
}
