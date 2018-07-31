package ch.swisssmp.mobcamps;

public class MobCampQuery {
	private final MobCamp mobCamp;
	private final int camp_id;
	private final boolean itemIsMobCampToken;
	
	protected MobCampQuery(MobCamp mobCamp, int camp_id, boolean isMobCampToken){
		this.mobCamp = mobCamp;
		this.camp_id = camp_id;
		this.itemIsMobCampToken = isMobCampToken;
	}
	/**
	 * Returns the mob camp associated with the item stack. If no camp was found this value is null.
	 */
	public MobCamp getMobCamp(){
		return this.mobCamp;
	}
	/**
	 * Returns the camp id. If no id was found, this value is -1.
	 */
	public int getMobCampId(){
		return this.camp_id;
	}
	/**
	 * Returns whether this item stack is associated with a mob camp (independant of whether the mob camp was actually found)
	 */
	public boolean isMobCampToken(){
		return this.itemIsMobCampToken;
	}
}
