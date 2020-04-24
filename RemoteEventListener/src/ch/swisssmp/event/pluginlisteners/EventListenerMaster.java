package ch.swisssmp.event.pluginlisteners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import ch.swisssmp.webcore.HTTPRequest;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import ch.swisssmp.event.EventListenerCreator;
import ch.swisssmp.event.RemoteEventListener;
import ch.swisssmp.event.remotelisteners.BasicEventListener;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class EventListenerMaster{
	
	private static EventListenerMaster instance;
	
	private HashMap<String,List<BasicEventListener>> eventListeners = new HashMap<String,List<BasicEventListener>>();
	private final boolean adventureDungeonsLoaded;
	private final boolean areaTransformationsLoaded;
	private final boolean wgRegionEventsLoaded;
	private boolean debug = false;
	
	private EventListenerMaster(){
		instance = this;
		this.adventureDungeonsLoaded = Bukkit.getPluginManager().isPluginEnabled("AdventureDungeons");
		this.areaTransformationsLoaded = Bukkit.getPluginManager().isPluginEnabled("AreaTransformations");
		this.wgRegionEventsLoaded = Bukkit.getPluginManager().isPluginEnabled("WGRegionEvents");
		if(this.adventureDungeonsLoaded){
			Bukkit.getPluginManager().registerEvents(new AdventureDungeonsListener(), RemoteEventListener.plugin);
		}
		if(this.areaTransformationsLoaded){
			Bukkit.getPluginManager().registerEvents(new AreaTransformationsListener(), RemoteEventListener.plugin);
		}
		if(this.wgRegionEventsLoaded){
			Bukkit.getPluginManager().registerEvents(new WGRegionEventsListener(), RemoteEventListener.plugin);
		}
	}
	
	public static EventListenerMaster init(){
		EventListenerMaster result = new EventListenerMaster();
		result.loadEventListeners((success)->{
			if(!success){
				Bukkit.getLogger().info(RemoteEventListener.getPrefix()+" Listener konnten nicht geladen werden.");
			}
		});
		return result;
	}
	
	protected void trigger(Event event){
		if(debug){
			Bukkit.getLogger().info("[RemoteEventListener] "+event.getEventName());
		}
		if(!eventListeners.containsKey(event.getEventName())) return;
		boolean cancelled = false;
		if(event instanceof Cancellable){
			cancelled = ((Cancellable)event).isCancelled();
		}
		for(BasicEventListener eventListener : eventListeners.get(event.getEventName())){
			if(cancelled && eventListener.getIgnoreCancelled()) continue;
			eventListener.trigger(event);
		}
	}
	
	public void loadEventListeners(Consumer<Boolean> callback){
		HTTPRequest request = DataSource.getResponse(RemoteEventListener.getInstance(), "events/remote_listeners.php");
		request.onFinish(()->{
			YamlConfiguration yamlConfiguration = request.getYamlResponse();
			if(yamlConfiguration==null){
				callback.accept(false);
				return;
			}
			eventListeners.clear();
			for(String key : yamlConfiguration.getKeys(false)){
				ConfigurationSection listenersSection = yamlConfiguration.getConfigurationSection(key);
				List<BasicEventListener> listeners = new ArrayList<BasicEventListener>();
				for(String listenerKey : listenersSection.getKeys(false)){
					BasicEventListener listener = EventListenerCreator.create(listenersSection.getConfigurationSection(listenerKey));
					if(listener!=null) listeners.add(listener);
				}
				eventListeners.put(key, listeners);
			}
			callback.accept(true);
		});
	}
	
	public boolean getAdventureDungeonsLoaded(){
		return this.adventureDungeonsLoaded;
	}
	
	public boolean getAreaTransformationsLoaded(){
		return this.areaTransformationsLoaded;
	}
	
	public boolean getWGRegionEventsLoaded(){
		return this.wgRegionEventsLoaded;
	}
	
	public void toggleDebug(){
		this.debug = !this.debug;
	}
	
	public boolean debugOn(){
		return debug;
	}
	
	public BasicEventListener[] getEventListeners(){
		List<BasicEventListener> listeners = new ArrayList<BasicEventListener>();
		for(List<BasicEventListener> listenerList : eventListeners.values()){
			listeners.addAll(listenerList);
		}
		return listeners.toArray(new BasicEventListener[listeners.size()]);
	}
	
	public static EventListenerMaster getInst(){
		return instance;
	}
}
