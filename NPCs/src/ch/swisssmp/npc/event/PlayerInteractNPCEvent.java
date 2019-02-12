package ch.swisssmp.npc.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;

import ch.swisssmp.npc.NPCInstance;

public class PlayerInteractNPCEvent extends PlayerNPCEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

	private final EquipmentSlot hand;
    
    private boolean cancelled = false;
    private boolean allowDefault = false;
	
	public PlayerInteractNPCEvent(Player who, NPCInstance npc, EquipmentSlot hand) {
		super(who,npc);
		this.hand = hand;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}
	
	public EquipmentSlot getHand(){
		return hand;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		this.cancelled = arg0;
	}
	
	public boolean preventDefault(){
		return !allowDefault;
	}
	
	public void setPreventDefault(boolean preventDefault){
		this.allowDefault = !preventDefault;
	}
}
