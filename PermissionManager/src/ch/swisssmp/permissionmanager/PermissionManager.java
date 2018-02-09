package ch.swisssmp.permissionmanager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import ch.swisssmp.tablist.TabList;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.webcore.DataSource;

public class PermissionManager extends JavaPlugin implements Listener{
	private static HashMap<UUID, PermissionAttachment> attachments = new HashMap<UUID, PermissionAttachment>();
	private static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static PermissionManager plugin;
	protected CommandListener pexCommandListener;
	
	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		
		this.getCommand("permission").setExecutor(new PlayerCommand());
		this.getCommand("permission").setTabCompleter(new CommandCompleter());
		this.getCommand("promote").setExecutor(new PlayerCommand());
		this.getCommand("promote").setTabCompleter(new CommandCompleter());
		this.getCommand("demote").setExecutor(new PlayerCommand());
		this.getCommand("demote").setTabCompleter(new CommandCompleter());
		this.pexCommandListener = new CommandListener();
		loadPermissions();
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
	private void onPlayerJoin(PlayerJoinEvent event){
		try {
			Player player = event.getPlayer();
			DataSource.getResponse("login.php", new String[]{
					"player_uuid="+player.getUniqueId().toString(),
					"player_name="+URLEncoder.encode(player.getName(), "utf-8"),
				});
			loadPermissions(player);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		attachments.remove(player.getUniqueId());
		DataSource.getResponse("logout.php", new String[]{
				"player_uuid="+player.getUniqueId().toString(),
			});
	}
	
	protected static void loadPermissions(){
		for(Player player : Bukkit.getOnlinePlayers()){
			loadPermissions(player);
		}
	}
	protected static void loadPermissions(Player player){
		try {
			YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("permissions/user.php", new String[]{
					"player="+player.getUniqueId().toString()
			});
			if(yamlConfiguration==null){
				Bukkit.getLogger().info("Permissions for "+player.getName()+" couldn't be loaded.");
				return;
			}
			if(yamlConfiguration.contains("permissions")){
				if(attachments.containsKey(player.getUniqueId())){
					PermissionAttachment oldAttachment = attachments.get(player.getUniqueId());
					player.removeAttachment(oldAttachment);
				}
				PermissionAttachment permissionAttachment = player.addAttachment(PermissionManager.plugin);
				attachments.put(player.getUniqueId(), permissionAttachment);
				ConfigurationSection permissionsSection = yamlConfiguration.getConfigurationSection("permissions");
				for(String key : permissionsSection.getKeys(false)){
					ConfigurationSection permissionSection = permissionsSection.getConfigurationSection(key);
					String permissionString = permissionSection.getString("permission");
					boolean value = permissionSection.getBoolean("value");
					Permission permission = Bukkit.getPluginManager().getPermission(permissionString);
					if(permission==null){
						if(debug) Bukkit.getLogger().info(permissionString+" isn't a registered permission.");
						permission = new Permission(permissionString);
					}
					permissionAttachment.setPermission(permission, value);
				}
				ConfigurationSection infosSection = yamlConfiguration.getConfigurationSection("infos");
				player.setOp(infosSection.getInt("operator")==1);
				player.recalculatePermissions();
				TabList.configurePlayer(player);
			}
			else{
				Bukkit.getLogger().info("Permissions for "+player.getName()+" couldn't be loaded.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
