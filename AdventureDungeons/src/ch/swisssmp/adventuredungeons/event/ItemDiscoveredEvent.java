package ch.swisssmp.adventuredungeons.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.adventuredungeons.world.DungeonInstance;

public class ItemDiscoveredEvent extends DungeonEvent{
    private static final HandlerList handlers = new HandlerList();
    
    private final Player player;
    private final ItemStack itemStack;
    
    public ItemDiscoveredEvent(DungeonInstance dungeonInstance, Player player, ItemStack itemStack){
    	super(dungeonInstance);
    	this.player = player;
    	this.itemStack = itemStack;
    }
    
    public Player getPlayer(){
    	return this.player;
    }
    
    public ItemStack getItemStack(){
    	return this.itemStack;
    }

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}
}
