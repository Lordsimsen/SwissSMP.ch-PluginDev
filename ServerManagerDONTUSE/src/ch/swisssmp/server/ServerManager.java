package ch.swisssmp.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

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
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.RequestMethod;

public class ServerManager extends JavaPlugin implements Listener{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static ServerManager plugin;
	
	protected static File configFile;
	protected static YamlConfiguration config;
	
	private static int server_id;
	private static String server_name;
	private static String motd;
	private static String greeting;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		
		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("servermanager").setExecutor(playerCommand);
		
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		configFile = new File(getDataFolder(), "config.yml");
		dataFolder = getDataFolder();
		try {
	        firstRun();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getScheduler().runTaskLater(this, new Runnable(){
			@Override
			public void run(){
				updatePluginInfos();
			}
		}, 0l);
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		HandlerList.unregisterAll((JavaPlugin)this);
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
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
			String encoding = "UTF-8";
			reload();
			try{
				String response = DataSource.getResponse("server/server_info.php", new String[]{
						"server_id="+URLEncoder.encode(String.valueOf(server_id),encoding),
						"server_version="+URLEncoder.encode(Bukkit.getBukkitVersion(),encoding),
						"server_ip="+URLEncoder.encode(Bukkit.getIp()+":"+Bukkit.getPort(),encoding),
				});
				if(!String.valueOf(server_id).equals(response)){
					if(response.isEmpty()){
						logger.info("Did not receive new Server ID. Please try again.");
						return;
					}
					server_id = Integer.parseInt(response);
					if(server_id>0){
						config.set("server_id", server_id);
						config.save(configFile);
					}
					else{
						logger.info("Server could not be registered!");
						Bukkit.getPluginManager().disablePlugin(this);
						return;
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
				Bukkit.getPluginManager().disablePlugin(this);
				return;
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
				try{
					informations = new ArrayList<String>();
					PluginDescriptionFile pluginDescriptionFile = plugin.getDescription();
					informations.add("name="+URLEncoder.encode(pluginDescriptionFile.getName(),encoding));
					description = pluginDescriptionFile.getDescription();
					if(description!=null)
						informations.add("description="+URLEncoder.encode(description,encoding));
					version = pluginDescriptionFile.getVersion();
					if(version!=null)
						informations.add("version="+URLEncoder.encode(version,encoding));
					authors = pluginDescriptionFile.getAuthors();
					if(authors!=null)
						informations.add("author="+URLEncoder.encode(String.join(", ", authors),encoding));
					website = pluginDescriptionFile.getWebsite();
					if(website!=null)
						informations.add("website="+URLEncoder.encode(website,encoding));
					informations.add("server_id="+server_id);
					
					subobject_id = 0;
					permissions = pluginDescriptionFile.getPermissions();
					if(permissions!=null){
						for(Permission permission : permissions){
							informations.add("permissions["+subobject_id+"][name]="+URLEncoder.encode(permission.getName(),encoding));
							informations.add("permissions["+subobject_id+"][description]="+URLEncoder.encode(permission.getDescription(),encoding));
							informations.add("permissions["+subobject_id+"][default]="+URLEncoder.encode(permission.getDefault().name(),encoding));
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
								informations.add("commands["+command+"]["+property+"]="+URLEncoder.encode(String.valueOf(commandData.get(property)), encoding));
							}
						}
					}
					
					infoArray = new String[informations.size()];
					
					DataSource.getResponse("server/plugin_info.php", informations.toArray(infoArray), RequestMethod.POST);
				}
				catch(Exception e){
					logger.info("[ServerManager] Error updating plugin info for "+plugin.getName());
					e.printStackTrace();
				}
			}
	}
	
	public void rename(String name){
		try {
			server_name = name;
			config.set("server_name", server_name);
			config.save(configFile);
			DataSource.getResponse("server/set_info.php", new String[]{
					"server_id="+URLEncoder.encode(String.valueOf(server_id), "UTF-8"),
					"server_name="+URLEncoder.encode(server_name, "UTF-8")
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    private void firstRun() throws Exception {
        if(!configFile.exists()){
        	configFile.getParentFile().mkdirs();
            copy(getResource("config.yml"), configFile);
        }
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
    public static void reload() {
        try {
        	config = YamlConfiguration.loadConfiguration(configFile);
    		server_id = config.getInt("server_id");
    		server_name = config.getString("server_name");
        } catch (Exception e) {
            e.printStackTrace();
        }
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("server/get_info.php", new String[]{
				"server="+server_id
		});
		if(yamlConfiguration==null||!yamlConfiguration.contains("server")) return;
		ConfigurationSection dataSection = yamlConfiguration.getConfigurationSection("server");
		motd = dataSection.getString("motd");
		greeting = dataSection.getString("greeting");
    }
}
