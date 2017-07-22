package ch.swisssmp.adventuredungeons.event;

import ch.swisssmp.adventuredungeons.world.Instancable;

public abstract class CampEvent extends DungeonEvent implements Instancable{
	private final int camp_id;
	private final int instance_id;
	
	public CampEvent(int dungeon_id, int instance_id, int camp_id){
		super(dungeon_id);
		this.instance_id = instance_id;
		this.camp_id = camp_id;
	}
	public int getInstanceId(){
		return this.instance_id;
	}
	public int getCampId(){
		return this.camp_id;
	}
	
}
