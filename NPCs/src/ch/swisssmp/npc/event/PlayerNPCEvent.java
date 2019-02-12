package ch.swisssmp.npc.event;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

import ch.swisssmp.npc.NPCInstance;

public abstract class PlayerNPCEvent extends PlayerEvent {

	private final NPCInstance npc;
	
	public PlayerNPCEvent(Player who, NPCInstance npc) {
		super(who);
		this.npc = npc;
	}

	public NPCInstance getNPC(){
		return npc;
	}
}
