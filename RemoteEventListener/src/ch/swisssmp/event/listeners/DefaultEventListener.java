package ch.swisssmp.event.listeners;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import ch.swisssmp.event.listeners.filter.CancelFilter;
import ch.swisssmp.utils.ConfigurationSection;

public class DefaultEventListener implements CancelFilter{
	protected final String[] commands;
	protected final ConfigurationSection dataSection;
	private final boolean ignoreCancelled;
	private final boolean cancelEvent;
	private final boolean setCancelled;
	
	public DefaultEventListener(ConfigurationSection dataSection){
		List<String> commandsList = dataSection.getStringList("commands");
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
	
	public void trigger(Event event){
		if(event instanceof Cancellable){
			if(!this.checkCancelled(this.dataSection, (Cancellable)event)) return;
			if(cancelEvent){
				((Cancellable)event).setCancelled(setCancelled);
			}
		}
		for(int i = 0; i < commands.length; i++){
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), insertArguments(commands[i],event));
		}
	}
	
	public boolean getIgnoreCancelled(){
		return this.ignoreCancelled;
	}
	
	protected void trigger(Event event, UUID player_uuid){
		for(int i = 0; i < commands.length; i++){
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), insertArguments(insertPlayer(commands[i], player_uuid),event));
		}
	}
	
	protected void trigger(Event event, Player player){
		for(int i = 0; i < commands.length; i++){
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), insertArguments(insertPlayer(commands[i], player),event));
		}
	}
	
	//meant to be overridden
	protected String insertArguments(String command, Event event){
		return command;
	}

	private String insertPlayer(String command, UUID player_uuid){
		Player player = Bukkit.getPlayer(player_uuid);
		if(player==null){
			return command.replace("{UUID}", player_uuid.toString());
		}
		else{
			return insertPlayer(command, player);
		}
	}
	
	private String insertPlayer(String command, Player player){
		if(player!=null){
			command = command.replace("{UUID}", player.getUniqueId().toString());
			command = command.replace("{Player}", player.getName());
			command = command.replace("{§Player}", player.getDisplayName());
			command = command.replace("{World}", player.getWorld().getName());
		}
		return command;
	}
	
}
