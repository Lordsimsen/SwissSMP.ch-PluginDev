package ch.swisssmp.event.remotelisteners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import ch.swisssmp.adventuredungeons.world.Dungeon;
import ch.swisssmp.adventuredungeons.world.DungeonInstance;
import ch.swisssmp.event.pluginlisteners.EventListenerMaster;
import ch.swisssmp.event.remotelisteners.filter.CancelFilter;
import ch.swisssmp.utils.ConfigurationSection;

public class BasicEventListener implements CancelFilter{
	private final String name;
	private final String type;
	protected final String[] commands;
	protected final ConfigurationSection dataSection;
	private final boolean ignoreCancelled;
	private final boolean cancelEvent;
	private final boolean setCancelled;
	
	public BasicEventListener(ConfigurationSection dataSection){
		List<String> commandsList = dataSection.getStringList("commands");
		this.name = dataSection.getString("name");
		this.type = dataSection.getString("type");
		this.commands = commandsList.toArray(new String[commandsList.size()]);
		this.dataSection = dataSection;
		if(this.dataSection.contains("ignore_cancelled"))
			this.ignoreCancelled = dataSection.getBoolean("ignore_cancelled");
		else this.ignoreCancelled = true;
		if(this.dataSection.contains("set_cancelled")){
			cancelEvent = true;
			this.setCancelled = dataSection.getBoolean("set_cancelled");
		}
		else{
			cancelEvent = false;
			this.setCancelled = false;
		}
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getType(){
		return this.type;
	}
	
	public void trigger(Event event){
		if(event instanceof Cancellable){
			if(!this.checkCancelled(this.dataSection, (Cancellable)event)) return;
			if(cancelEvent){
				((Cancellable)event).setCancelled(setCancelled);
			}
		}
		String command;
		for(int i = 0; i < commands.length; i++){
			command = insertArguments(commands[i],event);
			if(EventListenerMaster.getInst().debugOn()){
				Bukkit.getLogger().info("[RemoteEventListener] Running command '"+command+"'!");
			}
			if(!Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)){
				this.reportError(command);
			}
		}
	}
	
	public boolean getIgnoreCancelled(){
		return this.ignoreCancelled;
	}
	
	protected void trigger(Event event, Player player){
		if(event instanceof Cancellable){
			if(!this.checkCancelled(this.dataSection, (Cancellable)event)) return;
			if(cancelEvent){
				((Cancellable)event).setCancelled(setCancelled);
			}
		}
		String command;
		for(int i = 0; i < commands.length; i++){
			command = insertArguments(insertPlayer(commands[i], player),event);
			if(EventListenerMaster.getInst().debugOn()){
				Bukkit.getLogger().info("[RemoteEventListener] Running command '"+command+"'!");
			}
			if(!Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)){
				this.reportError(command);
			}
		}
	}
	
	private void reportError(String command){
		Bukkit.getLogger().info("[RemoteEventListener] Befehl wurde nicht ordnungsgemäss ausgeführt: "+command);
	}
	
	//meant to be overridden
	protected String insertArguments(String command, Event event){
		return command;
	}
	
	private String insertPlayer(String command, Player player){
		if(player!=null){
			command = command.replace("{UUID}", player.getUniqueId().toString());
			command = command.replace("{Player}", player.getName());
			command = command.replace("{§Player}", player.getDisplayName());
			command = command.replace("{World}", player.getWorld().getName());
			if(command.contains("{Instance-ID}")){
				DungeonInstance dungeonInstance = Dungeon.getInstance(player);
				if(dungeonInstance!=null){
					command = command.replace("{Instance-ID}", String.valueOf(dungeonInstance.getInstanceId()));
				}
			}
		}
		return command;
	}
	
}
