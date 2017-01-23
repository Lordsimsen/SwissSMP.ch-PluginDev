package ch.swisssmp.chatlogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

public class ChatEvent implements Listener{


	static ChatLogger plugin;
	private List<Pattern> patterns;

	public ChatEvent(ChatLogger plugin){
		ChatEvent.plugin = plugin;
		patterns = loadPatterns();
		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvents(this, plugin);	
	}


	public List<Pattern> loadPatterns(){

		List<String> stringPatterns = plugin.getConfig().getStringList("StringsToIgnore");
		List<Pattern> patterns = new LinkedList<Pattern>();

		for (String s : stringPatterns){

			patterns.add(Pattern.compile(s, Pattern.CASE_INSENSITIVE));

		}

		return patterns;
	}




	@EventHandler
	public void onChatEvent(AsyncPlayerChatEvent event){
		Boolean isGuest = false;
		Boolean match = false;
		String message = event.getMessage();
		Player player = event.getPlayer();
		isGuest = !player.hasPermission("steve.teach");




		for (Pattern p : patterns){
			Matcher matcher = p.matcher(message);

			if (matcher.find()){
				match = true;
				break;
			}

		}


		try(FileWriter fw = new FileWriter(plugin.getDataFolder()+ File.separator + "ChatLog.txt", true);BufferedWriter bw = new BufferedWriter(fw);PrintWriter out = new PrintWriter(bw))
			{
			plugin.getServer().broadcastMessage("test1");
				if (!message.toLowerCase().contains("steve")){
					if (!isGuest && !match ){
						out.println(message);
						plugin.clm.increaseNumberOfLinesByOne();
						plugin.getServer().broadcastMessage("test2");
					}
					else if(isGuest)plugin.getServer().broadcastMessage("test guest");
					else if(match)plugin.getServer().broadcastMessage("test match");
				}
				else{

					plugin.getServer().broadcastMessage("test3");
					int n = plugin.clm.getNumberOfLines();
					int currentLineNumber = plugin.clm.getRandomNumber(n);			
	
					new BukkitRunnable() {
	
						@Override
						public void run() {
	
							String SayBack;
							try {
								SayBack =  "["+ChatColor.GRAY +"Steve"+ChatColor.WHITE +"] "  + plugin.clm.getLineNumberN(currentLineNumber);
								plugin.getServer().broadcastMessage(SayBack);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	
						}
	
					}.runTaskLater(plugin, 20);
				}
			} catch (IOException e) {	
				plugin.getServer().getLogger().info(e.getMessage());
		}
	}


}