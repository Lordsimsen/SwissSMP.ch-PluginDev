package ch.swisssmp.loginrewards;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public class LoginRewards extends JavaPlugin{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static LoginRewards plugin;
	
	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		
		Bukkit.getPluginCommand("loginrewards").setExecutor(new PlayerCommand());
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	protected static void trigger(Player player){
		HTTPRequest request = DataSource.getResponse(plugin, "trigger_rewards.php", new String[]{
				"player_uuid="+player.getUniqueId().toString()
		});
		request.onFinish(()->{
			trigger(request.getYamlResponse(), player);
		});
	}
	
	private static void trigger(YamlConfiguration yamlConfiguration, Player player){
		if(yamlConfiguration==null || !yamlConfiguration.contains("message")) return;
		for(String line : yamlConfiguration.getStringList("message")){
			SwissSMPler.get(player).sendRawMessage(line);
		}
	}
	
	protected static void reset(){
		DataSource.getResponse(plugin, "reset_rewards.php");
	}
}
