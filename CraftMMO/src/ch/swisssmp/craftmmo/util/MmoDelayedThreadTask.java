package ch.swisssmp.craftmmo.util;

public class MmoDelayedThreadTask implements Runnable{
	public final Runnable runnable;
	public MmoDelayedThreadTask(Runnable runnable){
		this.runnable = runnable;
	}
	@Override
	public void run() {
		if(runnable!=null)runnable.run();
	}
}
