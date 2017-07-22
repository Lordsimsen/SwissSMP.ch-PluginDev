package ch.swisssmp.adventuredungeons.camp;

public class CampCleanupTask implements Runnable{
	
	private final Camp mmoCamp;
	
	public CampCleanupTask(Camp mmoCamp){
		this.mmoCamp = mmoCamp;
	}
	
	@Override
	public void run(){
		if(!mmoCamp.isActive() || !mmoCamp.isSpawning()){
			this.mmoCamp.despawnMobs();
		}
	}
}
