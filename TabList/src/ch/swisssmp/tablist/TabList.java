package ch.swisssmp.tablist;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public class TabList implements Listener{
	
	protected static boolean debug = false;
	
	public static HTTPRequest configurePlayer(Player player){
		return configurePlayer(player, false);
	}
	
	public static HTTPRequest configurePlayer(Player player, boolean joining){
		if(player==null) return null;
		HTTPRequest request = DataSource.getResponse(TabListPlugin.getInstance(), "info.php", new String[]{
				"player="+player.getUniqueId().toString(),
				"name="+URLEncoder.encode(player.getName())
			});
		request.onFinish(()->{
			configurePlayer(request.getYamlResponse(), player);
			if(!joining) return;
			Bukkit.broadcastMessage(ChatColor.RESET+"["+ChatColor.GREEN+"+"+ChatColor.RESET+"] "+player.getDisplayName());
		});
		return request;
	}
	
	private static void configurePlayer(YamlConfiguration yamlConfiguration, Player player){
		ConfigurationSection headerSection = yamlConfiguration.getConfigurationSection("header");
		String header = getChatString(headerSection);
		ConfigurationSection footerSection = yamlConfiguration.getConfigurationSection("footer");
		String footer = getChatString(footerSection);
		ConfigurationSection userSection = yamlConfiguration.getConfigurationSection("user");
		String user;
		String fullDisplayName;
		if(yamlConfiguration.getInt("rank")>1){
			user = getChatString(userSection);
			fullDisplayName = user+ChatColor.RESET;
		}
		else{
			user = userSection.getString("text");
			ChatColor color = ChatColor.valueOf(userSection.getString("color"));
			fullDisplayName = color+"[Gast]"+ChatColor.WHITE+" "+user+ChatColor.RESET;
		}
		if(debug){
			Bukkit.getLogger().info("Header: "+header);
			Bukkit.getLogger().info("Footer: "+footer);
			Bukkit.getLogger().info("Spielername: "+user);
		}
		player.setDisplayName(fullDisplayName);
		player.setPlayerListName(fullDisplayName);
		player.setPlayerListHeaderFooter(header, footer);
	}
	
	private static String getChatString(ConfigurationSection dataSection){
		if(dataSection==null) return "";
		if(dataSection.getString("text")==null) return "";
		if(dataSection.getString("text").equals("null")) return "";
		ChatColor color = ChatColor.valueOf(dataSection.getString("color"));
		return color+dataSection.getString("text");
	}
}
