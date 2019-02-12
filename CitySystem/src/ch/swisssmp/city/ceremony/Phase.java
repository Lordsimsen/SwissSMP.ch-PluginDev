package ch.swisssmp.city.ceremony;

public abstract class Phase {
	private boolean completed = false;
	
	protected void setCompleted(){
		completed = true;
	}
	
	public boolean isCompleted(){
		return completed;
	}
	
	public abstract void run();
	public void begin(){}
	public void complete(){}
	public void cancel(){}
	public void finish(){}
}
