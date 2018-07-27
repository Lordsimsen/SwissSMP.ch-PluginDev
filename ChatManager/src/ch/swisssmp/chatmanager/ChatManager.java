package ch.swisssmp.chatmanager;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.webcore.DataSource;

public class ChatManager extends JavaPlugin implements Listener{
	private static Logger logger;
	private static Server server;
	private static PluginDescriptionFile pdfFile;
	
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
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onPlayerJoin(PlayerJoinEvent event){
		String player_uuid = event.getPlayer().getUniqueId().toString();
		String name = event.getPlayer().getName();
		String world = event.getPlayer().getWorld().getName();
		String message = "joined";
		DataSource.getResponse("chat_notifications/chat.php", new String[]{
			"player_uuid="+URLEncoder.encode(player_uuid),
			"name="+URLEncoder.encode(name),
			"world="+URLEncoder.encode(world),
			"message="+URLEncoder.encode(message)
		});
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onPlayerQuit(PlayerQuitEvent event){
		String player_uuid = event.getPlayer().getUniqueId().toString();
		String name = event.getPlayer().getName();
		String world = event.getPlayer().getWorld().getName();
		String message = "quit";
		DataSource.getResponse("chat_notifications/chat.php", new String[]{
			"player_uuid="+URLEncoder.encode(player_uuid),
			"name="+URLEncoder.encode(name),
			"world="+URLEncoder.encode(world),
			"message="+URLEncoder.encode(message)
		});
	}
	
	@EventHandler(ignoreCancelled=true,priority=EventPriority.LOWEST)
	private void onChat(AsyncPlayerChatEvent event){
		String player_uuid = event.getPlayer().getUniqueId().toString();
		String name = event.getPlayer().getName();
		String world = event.getPlayer().getWorld().getName();
		String message = event.getMessage();
		DataSource.getResponse("chat_notifications/chat.php", new String[]{
			"player_uuid="+URLEncoder.encode(player_uuid),
			"name="+URLEncoder.encode(name),
			"world="+URLEncoder.encode(world),
			"message="+URLEncoder.encode(message)
		});
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onPlayerCommand(PlayerCommandPreprocessEvent event){
		String message = event.getMessage();
		String[] alertCommands = new String[]{
			"/msg ",
			"/w ",
			"/t ",
			"/pm ",
			"/emsg ",
			"/epm ",
			"/tell ",
			"/etell ",
			"/whisper ",
			"/ewhisper ",
			"/m ",
			"/r ",
			"/a "
		};
		boolean isAlertCommand = false;
		for(String s : alertCommands){
			if(message.toLowerCase().contains(s)){
				isAlertCommand = true;
				break;
			}
		}
		if(isAlertCommand){
			String recipient = message.split(" ")[1];
			Player player = Bukkit.getPlayer(recipient);
			if(player==null) return;
			if(player.hasPermission("chatnotifier.personalalert") || event.getPlayer().hasPermission("chatnotifier.personalalert")){
				String player_uuid = event.getPlayer().getUniqueId().toString();
				String name = event.getPlayer().getName();
				String world = event.getPlayer().getWorld().getName();
				message = extractMessage(message);
				DataSource.getResponse("chat_notifications/chat.php", new String[]{
					"player_uuid="+URLEncoder.encode(player_uuid),
					"name="+URLEncoder.encode(name),
					"world="+URLEncoder.encode(world),
					"message="+URLEncoder.encode(message),
					"recipient="+URLEncoder.encode(player.getName())
				});
			}
		}
	}
	
	private String extractMessage(String command){
	    String[] messageParts = command.split(" ");
	    int offset = messageParts[0].length()+messageParts[1].length()+1;
	    if(command.length()<=offset) return "";
	    return command.substring(offset);
	}
}
