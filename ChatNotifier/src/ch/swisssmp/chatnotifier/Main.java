package ch.swisssmp.chatnotifier;

import java.net.URLEncoder;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.webcore.DataSource;

public class Main extends JavaPlugin implements Listener{
	private static Logger logger;
	private static Server server;
	private static PluginDescriptionFile pdfFile;
	private static int repeatingTaskID;
	
	//private String[] keywords = new String[0];
	
	@Override
	public void onEnable() {
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		server = getServer();
		
		server.getPluginManager().registerEvents(this, this);
		this.getCommand("chat").setExecutor(new PlayerChat());
	}
    
	@Override
	public void onDisable() {
        Bukkit.getScheduler().cancelTask(repeatingTaskID);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onChat(AsyncPlayerChatEvent event){
		String player_uuid = event.getPlayer().getUniqueId().toString();
		String name = event.getPlayer().getName();
		String world = event.getPlayer().getWorld().getName();
		String message = event.getMessage();
		try{
		DataSource.getResponse("chat_notifications/.php", new String[]{
			"player_uuid="+player_uuid,
			"name="+name,
			"world="+world,
			"message="+URLEncoder.encode(message, "utf-8")
		});
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onPlayerCommand(PlayerCommandPreprocessEvent event){
		String message = event.getMessage();
		String[] alertCommands = new String[]{
			"/msg ",
			"/tell ",
			"/w ",
			"/m "
		};
		boolean isAlertCommand = false;
		for(String s : alertCommands){
			if(message.toLowerCase().contains(s)){
				event.setMessage("/tell "+message.substring(s.length()));
				isAlertCommand = true;
				break;
			}
		}
		if(isAlertCommand){
			String recipient = message.split(" ")[1];
			Player player = Bukkit.getPlayer(recipient);
			if(player==null) return;
			if(player.hasPermission("chatnotifier.personalalert")){
				try{
					String player_uuid = event.getPlayer().getUniqueId().toString();
					String name = event.getPlayer().getName();
					String world = event.getPlayer().getWorld().getName();
					message = "(an "+recipient+") "+event.getMessage().substring("/tell".length()+recipient.length()+2);
					DataSource.getResponse("chat_notifications/chat.php", new String[]{
						"player_uuid="+player_uuid,
						"name="+name,
						"world="+world,
						"message="+URLEncoder.encode(message, "utf-8")
					});
					}
					catch(Exception e){
						e.printStackTrace();
					}
			}
		}
	}
}
