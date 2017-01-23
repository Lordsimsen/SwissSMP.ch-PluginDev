package ch.swisssmp.adventuredungeons.mmoevent;

import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.adventuredungeons.Main;

public class MmoActionEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
	
	private final MmoAction mmoAction;
	private final UUID player_uuid;
	private final ItemStack itemStack;
	private final Block block;
	
	public MmoActionEvent(MmoAction mmoAction, UUID player_uuid, ItemStack itemStack, Block block){
		this.mmoAction = mmoAction;
		this.player_uuid = player_uuid;
		this.itemStack = itemStack;
		this.block = block;
		Main.info("Action Event called");
	}
	
	public MmoAction getMmoAction(){
		return this.mmoAction;
	}
	
	public UUID getPlayerUUID(){
		return this.player_uuid;
	}
	
	public ItemStack getMmoItemStack(){
		return this.itemStack;
	}
	
	public Block getClickedBlock(){
		return this.block;
	}

	@Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}