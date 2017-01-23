package ch.swisssmp.permissionmanager;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import ch.swisssmp.utils.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.tablist.TabList;
import ch.swisssmp.webcore.DataSource;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PermissionManager extends JavaPlugin{
	private static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static PermissionsEx permissionsExPlugin;
	protected static PermissionManager plugin;
	protected PexCommandListener pexCommandListener;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		
		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("permission").setExecutor(playerCommand);
		this.pexCommandListener = new PexCommandListener();
		
		Plugin pexPlugin = PermissionsEx.getPlugin();
		if(pexPlugin instanceof PermissionsEx)
			permissionsExPlugin = (PermissionsEx)pexPlugin;
		else{
			throw new NullPointerException("Pex couldn't be linked!");
		}
		loadPermissions();
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	protected static void loadPermissions(){
		try {
			YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("permissions/permissions.php");
			if(yamlConfiguration==null){
				Bukkit.getLogger().info("Permissions couldn't be loaded. Trying again in 5 minutes.");
				return;
			}
			yamlConfiguration.save(new File(permissionsExPlugin.getDataFolder(), "permissions.yml"));
			PluginCommand command = permissionsExPlugin.getCommand("pex");
			if(command!=null){
				command.execute(Bukkit.getConsoleSender(), "pex", new String[]{"reload"});
			}
			for(Player player : Bukkit.getOnlinePlayers()){
				TabList.configurePlayer(player);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
