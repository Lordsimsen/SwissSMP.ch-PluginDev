package ch.swisssmp.craftmmo.mmoevent;

import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmoitem.MmoItemStack;

public class MmoActionEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
	
	private final MmoAction mmoAction;
	private final UUID player_uuid;
	private final MmoItemStack mmoItemStack;
	private final Block block;
	
	public MmoActionEvent(MmoAction mmoAction, UUID player_uuid, MmoItemStack mmoItemStack, Block block){
		this.mmoAction = mmoAction;
		this.player_uuid = player_uuid;
		this.mmoItemStack = mmoItemStack;
		this.block = block;
		Main.info("Action Event called");
	}
	
	public MmoAction getMmoAction(){
		return this.mmoAction;
	}
	
	public UUID getPlayerUUID(){
		return this.player_uuid;
	}
	
	public MmoItemStack getMmoItemStack(){
		return this.mmoItemStack;
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