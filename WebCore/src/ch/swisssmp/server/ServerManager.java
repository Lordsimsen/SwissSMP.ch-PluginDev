package ch.swisssmp.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.RequestMethod;
import ch.swisssmp.webcore.WebCore;

public class ServerManager implements Listener{
	private static ServerManager instance;
	
	private JavaPlugin plugin;
	
	private static int server_id = -1;
	private static String server_name;
	
	private String motd;
	private String greeting;
	
	public ServerManager(JavaPlugin plugin){
		if(instance!=null)
		{
			HandlerList.unregisterAll(instance);
		}
		instance = this;
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
		Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable(){
			@Override
			public void run(){
				updatePluginInfos();
			}
		}, 0l);
	}
	
	@EventHandler
	private void onServerPing(ServerListPingEvent event){
		if(motd==null || motd.isEmpty()) return;
		event.setMotd(motd);
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onPlayerJoin(PlayerJoinEvent event){
		if(greeting==null || greeting.isEmpty()) return;
		Player player = event.getPlayer();
		player.sendMessage(greeting);
		player.performCommand("list");
	}
	
	public void updatePluginInfos(){
			reload();
			String response = DataSource.getResponse("server/set_info.php", new String[]{
					"server_version="+URLEncoder.encode(Bukkit.getBukkitVersion()),
					"server_ip="+URLEncoder.encode(Bukkit.getIp()+":"+Bukkit.getPort()),
			});
			if(!String.valueOf(server_id).equals(response)){
				if(response.isEmpty()){
					WebCore.info("Did not receive new Server ID. Please try again.");
					return;
				}
				server_id = Integer.parseInt(response);
				if(server_id>0){
					WebCore.getInstance().saveConfig();
				}
				else{
					WebCore.info("Server could not be registered!");
					return;
				}
			}
			
			PluginManager pluginManager = Bukkit.getPluginManager();
			Plugin[] plugins = pluginManager.getPlugins();
			List<String> informations;
			List<Permission> permissions;
			Set<String> commands;
			String[] infoArray;
			int subobject_id;
			String description;
			String version;
			List<String> authors;
			String website;
			for(Plugin plugin : plugins){
				informations = new ArrayList<String>();
				PluginDescriptionFile pluginDescriptionFile = plugin.getDescription();
				informations.add("name="+URLEncoder.encode(pluginDescriptionFile.getName()));
				description = pluginDescriptionFile.getDescription();
				if(description!=null)
					informations.add("description="+URLEncoder.encode(description));
				version = pluginDescriptionFile.getVersion();
				if(version!=null)
					informations.add("version="+URLEncoder.encode(version));
				authors = pluginDescriptionFile.getAuthors();
				if(authors!=null)
					informations.add("author="+URLEncoder.encode(String.join(", ", authors)));
				website = pluginDescriptionFile.getWebsite();
				if(website!=null)
					informations.add("website="+URLEncoder.encode(website));
				informations.add("server_id="+server_id);
				
				subobject_id = 0;
				permissions = pluginDescriptionFile.getPermissions();
				if(permissions!=null){
					for(Permission permission : permissions){
						informations.add("permissions["+subobject_id+"][name]="+URLEncoder.encode(permission.getName()));
						informations.add("permissions["+subobject_id+"][description]="+URLEncoder.encode(permission.getDescription()));
						informations.add("permissions["+subobject_id+"][default]="+URLEncoder.encode(permission.getDefault().name()));
						subobject_id++;
					}
				}

				subobject_id = 0;
				Map<String,Map<String,Object>> commandsMap = pluginDescriptionFile.getCommands();
				if(commandsMap!=null){
					commands = commandsMap.keySet();
					Map<String,Object> commandData;
					for(String command : commands){
						commandData = commandsMap.get(command);
						for(String property : commandData.keySet()){
							informations.add("commands["+command+"]["+property+"]="+URLEncoder.encode(String.valueOf(commandData.get(property))));
						}
					}
				}
				
				infoArray = new String[informations.size()];
				
				DataSource.getResponse("server/plugin_info.php", informations.toArray(infoArray), RequestMethod.POST);
			}
	}
	
	public int getServerId(){
		return server_id;
	}
	
	public String getServerName(){
		return server_name;
	}
	
	public void rename(String name){
		server_name = name;
		WebCore.getInstance().saveConfig();
		DataSource.getResponse("server/set_info.php", new String[]{
				"server_name="+URLEncoder.encode(server_name)
		});
	}
    public void reload() {
        try {
        	int old_server_id = server_id;
    		server_id = WebCore.getInstance().getConfigServerId();
    		server_name = WebCore.getInstance().getConfigServerName();
    		if(old_server_id!=server_id && server_id>0){
				DataSource.getResponse("session/start.php");
    		}
        } catch (Exception e) {
            e.printStackTrace();
        }
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("server/get_info.php", new String[]{
				"server="+server_id
		});
		if(yamlConfiguration==null||!yamlConfiguration.contains("server")) return;
		ConfigurationSection dataSection = yamlConfiguration.getConfigurationSection("server");
		Bukkit.getLogger().info(Bukkit.getBukkitVersion());
		String version = Bukkit.getBukkitVersion();
		motd = dataSection.getString("motd").replace("{newline}", "\n").replace("{version}", version);
		greeting = dataSection.getString("greeting");
    }
    
    public static ServerManager getInstance(){
    	return instance;
    }
}
