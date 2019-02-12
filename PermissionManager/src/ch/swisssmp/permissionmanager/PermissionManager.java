package ch.swisssmp.permissionmanager;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public class PermissionManager extends JavaPlugin implements Listener{
	private static HashMap<UUID, PermissionAttachment> attachments = new HashMap<UUID, PermissionAttachment>();
	private static Logger logger;
	private static PluginDescriptionFile pdfFile;
	private static PermissionManager plugin;
	
	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		
		this.getCommand("permission").setExecutor(new PlayerCommand());
		this.getCommand("promote").setExecutor(new PlayerCommand());
		this.getCommand("demote").setExecutor(new PlayerCommand());
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
		Player player = event.getPlayer();
		DataSource.getResponse(PermissionManager.getInstance(), "login.php", new String[]{
				"player_uuid="+player.getUniqueId().toString(),
				"player_name="+URLEncoder.encode(player.getName()),
			});
		loadPermissions(player, true);
	}
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		attachments.remove(player.getUniqueId());
		DataSource.getResponse(PermissionManager.getInstance(), "logout.php", new String[]{
				"player_uuid="+player.getUniqueId().toString(),
			});
	}
	@EventHandler
	private void onPlayerTabComplete(TabCompleteEvent event){
		CommandSender sender = event.getSender();
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;
		String chatMessage = event.getBuffer();
		if(chatMessage.trim().charAt(0) == '/'){
			if(!chatMessage.contains(" ") && !player.hasPermission("permissionmanager.commands.autocomplete")){
				event.getCompletions().clear();
			}
		}
	}
	
	protected static void loadPermissions(){
		for(Player player : Bukkit.getOnlinePlayers()){
			loadPermissions(player);
		}
	}
	protected static void loadPermissions(Player player){
		loadPermissions(player, false);
	}
	protected static void loadPermissions(Player player, boolean joining){
		HTTPRequest request = DataSource.getResponse(PermissionManager.getInstance(), "user.php", new String[]{
				"player="+player.getUniqueId().toString(),
				"world="+URLEncoder.encode(Bukkit.getWorlds().get(0).getName())
		});
		request.onFinish(()->{
			loadPermissions(request.getYamlResponse(), player, joining);
		});
	}
	
	private static void loadPermissions(YamlConfiguration yamlConfiguration, Player player, boolean joining){
		try {
			if(yamlConfiguration==null){
				Bukkit.getLogger().info("Permissions for "+player.getName()+" couldn't be loaded.");
				return;
			}
			if(yamlConfiguration.contains("permissions")){
				if(attachments.containsKey(player.getUniqueId())){
					try{
						PermissionAttachment oldAttachment = attachments.get(player.getUniqueId());
						player.removeAttachment(oldAttachment);
					}
					catch(Exception e){
						e.printStackTrace();
					}
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
				Bukkit.getPluginManager().callEvent(new PlayerPermissionsChangedEvent(player, joining));
			}
			else{
				Bukkit.getLogger().info("Permissions for "+player.getName()+" couldn't be loaded.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static PermissionManager getInstance(){
		return plugin;
	}
}
