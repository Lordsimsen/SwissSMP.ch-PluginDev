package ch.swisssmp.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerRenameItemEvent extends PlayerEvent implements Cancellable{
    private static final HandlerList handlers = new HandlerList();

    private final ItemStack itemStack;
    private String newName;
    private boolean cancelled = false;
    
    public PlayerRenameItemEvent(Player player, ItemStack itemStack, String newName){
    	super(player);
    	this.itemStack = itemStack;
    	this.newName = newName;
    }
    
    public ItemStack getItemStack(){
    	return this.itemStack;
    }
    
    public String getNewName(){
    	return this.newName;
    }
    
    public void setName(String name){
    	this.newName = name;
    }
    
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}
}
