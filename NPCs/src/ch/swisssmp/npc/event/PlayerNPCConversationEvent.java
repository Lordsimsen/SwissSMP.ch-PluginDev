package ch.swisssmp.npc.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.npc.conversations.NPCConversation;

public class PlayerNPCConversationEvent extends PlayerNPCEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

	private final NPCConversation conversation;
	private boolean cancelled = false;
	
	public PlayerNPCConversationEvent(Player who, NPCInstance npc, NPCConversation conversation) {
		super(who, npc);
		this.conversation = conversation;
	}
	
	public NPCConversation getConversation(){
		return conversation;
	}
	
	public String getPreviousLine(){
		return conversation.getLine(0);
	}
	
	public String getNextLine(){
		return conversation.getLine(1);
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		this.cancelled = arg0;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}


	public static HandlerList getHandlerList(){
		return handlers;
	}
}
