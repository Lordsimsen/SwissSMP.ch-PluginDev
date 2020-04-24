package ch.swisssmp.permissionmanager;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public class PermissionManager {
	
	private static HashMap<UUID, PermissionAttachment> attachments = new HashMap<UUID, PermissionAttachment>();
	
	protected static boolean debug = false;

	protected static void loadPermissions(){
		for(Player player : Bukkit.getOnlinePlayers()){
			loadPermissions(player);
		}
	}
	protected static void loadPermissions(Player player){
		loadPermissions(player, false);
	}
	protected static void loadPermissions(Player player, boolean joining){
		HTTPRequest request = DataSource.getResponse(PermissionManagerPlugin.getInstance(), "user.php", new String[]{
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
				PermissionAttachment permissionAttachment = player.addAttachment(PermissionManagerPlugin.getInstance());
				add(player.getUniqueId(), permissionAttachment);
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
				player.updateCommands();
				Bukkit.getPluginManager().callEvent(new PlayerPermissionsChangedEvent(player, joining));
			}
			else{
				Bukkit.getLogger().info("Permissions for "+player.getName()+" couldn't be loaded.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected static void add(UUID playerUid, PermissionAttachment attachment) {
		attachments.put(playerUid, attachment);
	}
	
	protected static void remove(UUID playerUid) {
		attachments.remove(playerUid);
	}
}
