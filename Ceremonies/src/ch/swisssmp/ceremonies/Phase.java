package ch.swisssmp.ceremonies;

public abstract class Phase {
	private boolean completed = false;
	private boolean cancelled = false;
	private boolean finished = false;
	
	protected void setCompleted(){
		completed = true;
	}
	
	public boolean isCompleted(){
		return completed;
	}
	public boolean isCancelled(){
		return cancelled;
	}
	public boolean isFinished(){
		return finished;
	}
	
	public abstract void run();
	public void begin(){}
	public void complete(){}
	public void cancel(){
		cancelled = true;
	}
	public void finish(){
		finished = true;
	}
}
