package ch.swisssmp.craftmmo.mmocamp;

public class MmoCampCleanupTask implements Runnable{
	
	private final MmoCamp mmoCamp;
	
	public MmoCampCleanupTask(MmoCamp mmoCamp){
		this.mmoCamp = mmoCamp;
	}
	
	@Override
	public void run(){
		if(!mmoCamp.isActive() || !mmoCamp.isSpawning()){
			this.mmoCamp.despawnMobs();
		}
	}
}
