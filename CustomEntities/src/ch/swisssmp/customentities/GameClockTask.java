package ch.swisssmp.customentities;

public class GameClockTask implements Runnable{

	@Override
	public void run() {
		for(CustomEntity entity : CustomEntity.getAll()){
			entity.Update();
		}
	}

}
