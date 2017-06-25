package ch.swisssmp.server;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.RequestMethod;
import ch.swisssmp.webcore.WebCore;

public class ServerManager{
	private static int server_id = -1;
	private static String server_name;
	
	public static void UpdatePluginInfos(){
			String encoding = "UTF-8";
			reload();
			try{
				String response = DataSource.getResponse("server/server_info.php", new String[]{
						"server_version="+URLEncoder.encode(Bukkit.getBukkitVersion(),encoding),
						"server_ip="+URLEncoder.encode(Bukkit.getIp()+":"+Bukkit.getPort(),encoding),
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
			}
			catch(Exception e){
				e.printStackTrace();
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
					WebCore.info("[WebCore] Error updating plugin info for "+plugin.getName());
					e.printStackTrace();
				}
			}
	}
	
	public static int getServerId(){
		return server_id;
	}
	
	public static String getServerName(){
		return server_name;
	}
	
	public static void Rename(String name){
		server_name = name;
		WebCore.getInstance().saveConfig();
		try {
			DataSource.getResponse("server/server_info.php", new String[]{
					"server_name="+URLEncoder.encode(server_name, "UTF-8")
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
    public static void reload() {
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
    }
}
