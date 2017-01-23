package ch.swisssmp.craftmmo.mmoentity;

import net.minecraft.server.v1_11_R1.EntityInsentient;

public class DelayedLocationLockingTask implements Runnable{
	
	public final IControllable iControllable;
	
	public DelayedLocationLockingTask(IControllable iControllable){
		this.iControllable = iControllable;
	}
	@Override
	public void run(){
		if(!iControllable.getMmoAI().isMobile()){
			EntityInsentient entity = iControllable.getEntity();
			iControllable.getSaveData().spawnpoint = new int[3];
			iControllable.getSaveData().spawnpoint[0] = (int) Math.round(entity.locX+0.5);
			iControllable.getSaveData().spawnpoint[1] = (int) Math.round(entity.locY);
			iControllable.getSaveData().spawnpoint[2] = (int) Math.round(entity.locZ+0.5);
		}
	}
}
