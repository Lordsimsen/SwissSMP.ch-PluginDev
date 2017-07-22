package ch.swisssmp.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.event.listeners.DefaultEventListener;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class RemoteEventListener extends JavaPlugin{

	public static RemoteEventListener plugin;
	public static Logger logger;
	public static Server server;
	public static PluginDescriptionFile pdfFile;
	
	private static HashMap<String,List<DefaultEventListener>> eventListeners = new HashMap<String,List<DefaultEventListener>>();
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	protected static void trigger(Event event){
		if(!eventListeners.containsKey(event.getEventName())) return;
		boolean cancelled = false;
		if(event instanceof Cancellable){
			cancelled = ((Cancellable)event).isCancelled();
		}
		for(DefaultEventListener eventListener : eventListeners.get(event.getEventName())){
			if(cancelled && eventListener.getIgnoreCancelled()) continue;
			eventListener.trigger(event);
		}
	}
	
	protected static boolean loadEventListeners(){
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("events/remote_listeners.php");
		if(yamlConfiguration==null) return false;
		eventListeners.clear();
		for(String key : yamlConfiguration.getKeys(false)){
			ConfigurationSection listenersSection = yamlConfiguration.getConfigurationSection(key);
			List<DefaultEventListener> listeners = new ArrayList<DefaultEventListener>();
			for(String listenerKey : listenersSection.getKeys(false)){
				listeners.add(EventListenerCreator.create(listenersSection.getConfigurationSection(listenerKey)));
			}
		}
		return true;
	}
    
	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
}
