package ch.swisssmp.craftmmo.mmoquest;

import ch.swisssmp.craftmmo.mmoentity.MmoMob;

public class MmoQuestHuntMob {
	public final int mmo_mob_id;
	public final int target_amount;
	public int current_amount = 0;
	
	public MmoQuestHuntMob(int mmo_mob_id, int target_amount){
		this.mmo_mob_id = mmo_mob_id;
		this.target_amount = target_amount;
	}
	
	public MmoMob getMmoMobTemplate(){
		return MmoMob.get(mmo_mob_id);
	}
}
