package ch.swisssmp.commandscheduler;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.webcore.DataSource;

public class CommandScheduler extends JavaPlugin{
	private static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static CommandScheduler plugin;
	
	private BukkitTask refreshTask;
	protected static boolean disable = false;
	private static int timeoutSeconds = 5;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		
		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("commandscheduler").setExecutor(playerCommand);
		refresh();
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		if(this.refreshTask!=null) this.refreshTask.cancel();
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	private void refresh(){
		try{
			if(!disable){
				runCommands();
			}
			else{
				Bukkit.getLogger().info("Skipped scheduled command execution. Retrying in "+timeoutSeconds+" seconds.");
			}
		}
		catch(Exception e){
			Bukkit.getLogger().info("Error executing remote commands! Continuing anyways, but please do check the log!");
			e.printStackTrace();
		}
		this.refreshTask = Bukkit.getScheduler().runTaskLater(this, new Runnable(){
			public void run(){
				refresh();
			}
		}, 20L*timeoutSeconds);
	}
	
	public static void runCommands(){
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("commands/commands.php");
		if(yamlConfiguration==null){
			Bukkit.getLogger().info("Commands couldn't be fetched. Trying again in "+timeoutSeconds+" seconds.");
			return;
		}
		for(String key : yamlConfiguration.getKeys(false)){
			ConfigurationSection commandSection = yamlConfiguration.getConfigurationSection(key);
			String commandLine = commandSection.getString("command");
			String sender = commandSection.getString("sender");
			List<String> argsList = commandSection.getStringList("args");
			String[] args;
			if(argsList==null){
				args = null;
			}
			else{
				args = new String[argsList.size()];
				argsList.toArray(args);
			}
			CommandSender commandSender;
			if(sender.equals("console")){
				commandSender = Bukkit.getConsoleSender();
			}
			else{
				Player player = Bukkit.getPlayer(UUID.fromString(sender));
				if(player==null) continue;
				commandSender = player;
			}
			try{
				Bukkit.dispatchCommand(commandSender, commandLine);
			}
			catch(Exception e){
				Bukkit.getLogger().info("Error executing command '"+commandLine+"' for "+sender);
				e.printStackTrace();
			}
		}
		if(yamlConfiguration.getKeys(false).size()>0){
			Bukkit.getLogger().info(yamlConfiguration.getKeys(false).size()+" commands executed.");
		}
	}
}
