package ch.swisssmp.bigdoors;

public class ResetDoorTask implements Runnable{

	public final String doorName;
	public final boolean open;
	
	public ResetDoorTask(String doorName, boolean open){
		this.doorName = doorName;
		this.open = open;
	}
	@Override
	public void run() {
		if(open)
			Door.open(doorName);
		else Door.close(doorName);
	}
}
