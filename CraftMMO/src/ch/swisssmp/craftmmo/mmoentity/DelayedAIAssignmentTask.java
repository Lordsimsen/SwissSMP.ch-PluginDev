package ch.swisssmp.craftmmo.mmoentity;

import net.minecraft.server.v1_11_R1.EntityInsentient;

public class DelayedAIAssignmentTask implements Runnable{

	private final MmoAI ai;
	private final EntityInsentient entity;
	
	public DelayedAIAssignmentTask(MmoAI ai, EntityInsentient entity){
		this.ai = ai;
		this.entity = entity;
	}
	
	@Override
	public void run() {
		if(entity instanceof IControllable){
			IControllable iControllable = (IControllable) entity;
			iControllable.setMmoAI(ai);
			MmoMob mmoMob = MmoMob.get(iControllable.getSaveData().mmo_mob_id);
			if(mmoMob!=null){
				if(mmoMob.invincible){
					MmoMob.invincible_mobs.put(entity.getUniqueID(), iControllable);
				}
			}
		}
	}

}
