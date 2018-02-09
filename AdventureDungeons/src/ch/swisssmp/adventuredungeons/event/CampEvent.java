package ch.swisssmp.adventuredungeons.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import ch.swisssmp.adventuredungeons.camp.Camp;

public abstract class CampEvent extends DungeonEvent{
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
	private final Camp camp;
	
	public CampEvent(Camp camp, Player player){
		super(camp.getDungeonInstance());
		this.camp = camp;
		this.player = player;
	}
	
	public Player getPlayer(){
		return this.player;
	}
	public int getInstanceId(){
		return this.camp.getDungeonInstance().getInstanceId();
	}
	public Camp getCamp(){
		return this.camp;
	}
	public int getCampId(){
		return this.camp.getCampId();
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}
}
